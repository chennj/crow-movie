package org.crow.movie.user.web.interceptor;

import java.io.PrintWriter;
import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.crow.movie.user.common.constant.Const;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.util.CookieUtil;
import org.crow.movie.user.common.util.DigestUtils;
import org.crow.movie.user.common.util.SessionUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.web.annotation.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 权限拦截，简易版
 * @author chenn
 *
 */
@Component
public class MemberPermissionInterceptor extends HandlerInterceptorAdapter implements InitializingBean {

	protected static final Logger logger = LoggerFactory.getLogger(MemberPermissionInterceptor.class.getClass());
	
	/**
	 * 方法白名单
	 */
	@Value("${movie.user.excludeUrl}")
	private String excludeUrl;
	
	private static String[] excludeUrls;

	/**
	 * 密码加盐
	 */
	@Value("${movie.user.salt}")
	private String salt;
	
	private static String pwdSalt;
			
	private static MemberInfoService memberInfoService;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		if (salt == null || salt.trim().length() == 0){
			throw new RuntimeException("Salt Cann't Be Empty! In Application Property File");
		}	

		pwdSalt = salt;

		if (excludeUrl == null || excludeUrl.trim().length() == 0){
			excludeUrls = new String[]{};
		} else {
			excludeUrls = excludeUrl.split(",");
		}

	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		boolean ajaxFlag 	= "XMLHttpRequest".equalsIgnoreCase(request.getHeader("x-requested-with"));
								
		if (!(handler instanceof HandlerMethod)) {
			return super.preHandle(request, response, handler);
		}
		
		/**
		 * 在请求方法(requestMethod)白名单中不进行拦截(例如isAlive)
		 */
		request.setCharacterEncoding("UTF-8");
		String contextName 		= request.getContextPath();
		String requestMethod 	= request.getRequestURI().replace(contextName, "");	
		
		for (int i = 0; i < excludeUrls.length; i++) {
			String excludeUrl = excludeUrls[i];
			if (requestMethod.startsWith(excludeUrl)) {
				return true;
			}
		}
		
		/**
		 * 检查是否是admin端内网接口，如果是直接返回
		 */
		HandlerMethod method = (HandlerMethod)handler;
		Permission permission = method.getMethodAnnotation(Permission.class);
		
		if (permission == null || permission.managerLimit()){
			return true;
		}
		
		/**
		 * 外网访问登录检查
		 */
		if (!ifMemberLogin(request)) {
			
			if (permission == null || permission.memberLimit()) {
				String js = request.getParameter("data");
				String username = null;
				String password = null;
				if (!StrUtil.isEmpty(js)){
					JSONObject jdata = JSON.parseObject(js);
					username = jdata.getString("account");
					password = jdata.getString("password");
				} else {
					username = request.getParameter("account");
					password = request.getParameter("password");
				}
				if (!login(response,request,username,password,false)){
					if (ajaxFlag){
						response.setContentType("application/json);charset=utf-8");
					}
					PrintWriter writer = null;
					try{
						writer = response.getWriter();
						writer.write("{\"code\":\"404\",\"msg\":\"需要先登录\",\"data\":null}");
						writer.flush();
					} catch (Exception e){
						logger.info("permission.preHandle>>>"+e.getMessage());
					} finally{
						try{
							if (null != writer){
								writer.close();
							}
						} catch(Exception e){}
					}
					
					return false;					
				}
			}
		}

		return super.preHandle(request, response, handler);
	}

    // ---------------------- tool ----------------------

	public static final String LOGIN_IDENTITY_KEY = "CROW_MOVIE_LOGIN_IDENTITY";
	
	public static boolean login(
			HttpServletResponse response, 
			HttpServletRequest request,
			String username,
			String password,
			boolean ifRemember){
				
		if (null == memberInfoService){
			WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
			memberInfoService = applicationContext.getBean(MemberInfoService.class);
			if (null == memberInfoService){
				throw new RuntimeException("User Service Bean Init Failed!");
			}
		}
		
		MemberInfo userInfo = memberInfoService.getUnique("account", username);
		if (null == userInfo){
			return false;
		}
		
		String pwd = DigestUtils.encryptMd5(DigestUtils.encryptMd5(password)+pwdSalt);
		if (!userInfo.getPassword().equals(pwd)){
			return false;
		}
		
		String tokenTmp = new BigInteger(1, pwd.getBytes()).toString(16);
		
		SessionUtil.setSession(Const.SESSION_USER_INFO_KEY, userInfo);
		CookieUtil.set(response, LOGIN_IDENTITY_KEY, tokenTmp, ifRemember);
		return true;
	}
	
	public static void logout(HttpServletRequest request, HttpServletResponse response){
		request.getSession().removeAttribute(Const.SESSION_USER_INFO_KEY);
		CookieUtil.remove(request, response, LOGIN_IDENTITY_KEY);
	}
	
	public static boolean ifMemberLogin(HttpServletRequest request){
		String indentityInfo = CookieUtil.getValue(request, LOGIN_IDENTITY_KEY);
		if (indentityInfo==null) {
			return false;
		}
		
		HttpSession session = request.getSession();
		MemberInfo user = (MemberInfo)session.getAttribute(Const.SESSION_USER_INFO_KEY);
		if (user == null){
			return false;
		}
		return true;
	}
	
}
