package org.crow.movie.user.web.interceptor;

import java.math.BigInteger;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.crow.movie.user.common.constant.Const;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.util.CookieUtil;
import org.crow.movie.user.common.util.DigestUtils;
import org.crow.movie.user.common.util.SessionUtil;
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

		if (!(handler instanceof HandlerMethod)) {
			return super.preHandle(request, response, handler);
		}
		
		/**
		 * 在请求方法(requestMethod)白名单中不进行拦截(例如isAlive)
		 */
		if (InterceptorFunc.IsUrlWhiteList(request, response, excludeUrls)){
			return true;
		}

		
		/**
		 * 检查是否是admin端内网接口，如果是直接返回
		 */
		boolean ajaxFlag 	= "XMLHttpRequest".equalsIgnoreCase(request.getHeader("x-requested-with"));
		
		HandlerMethod method = (HandlerMethod)handler;
		Permission permission = method.getMethodAnnotation(Permission.class);
		
		if (permission == null || permission.managerLimit()){
			return true;
		}
		
		// referer 拦截器防御CSRF攻击
		// 小知识：
		// 如下：其中Web A为存在CSRF漏洞的网站，Web B为攻击者构建的恶意网站，User C为Web A网站的合法用户。
		// 首先用户C浏览并登录了受信任站点A；
		// 登录信息验证通过以后，站点A会在返回给浏览器的信息中带上已登录的cookie，cookie信息会在浏览器端保存一定时间（根据服务端设置而定）；
		// 完成这一步以后，用户在没有登出（清除站点A的cookie）站点A的情况下，访问恶意站点B；
		// 这时恶意站点 B的某个页面向站点A发起请求，而这个请求会带上浏览器端所保存的站点 A 的cookie；
		// 站点A根据请求所带的 cookie，判断此请求为用户C所发送的。
		// 因此，站点A会报据用户C的权限来处理恶意站点B所发起的请求，而这个请求可能以用户C的身份发送 邮件、短信、消息，
		// 以及进行转账支付等操作，这样恶意站点B就达到了伪造用户C请求站点 A的目的。
		// 受害者只需要做下面两件事情，攻击者就能够完成CSRF攻击：
		//
		// 登录受信任站点 A，并在本地生成cookie；
		// 在不登出站点A（清除站点A的cookie）的情况下，访问恶意站点B。
		
		String referer 	= request.getHeader("Referer");
		String host 	= request.getServerName();
		if (referer == null){
			if (ajaxFlag){
				response.setContentType("application/json;charset=utf-8");
			}
			return InterceptorFunc.FAIL(response,"{\"code\":\"404\",\"msg\":\"不能在地址栏直接访问\"}");
		}

        java.net.URL url = null;
        try {
            url = new java.net.URL(referer);
        } catch (MalformedURLException e) {
            return InterceptorFunc.FAIL(response,"{\"code\":\"404\",\"msg\":\"URL解析异常\"}");
        }
        // 首先判断请求域名和referer域名是否相同
        if (!host.equals(url.getHost())) {
            return InterceptorFunc.FAIL(response,"{\"code\":\"404\",\"msg\":\"拒绝访问\"}");
        }

		/**
		 * 外网访问登录检查
		 */
		/*
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
					
					return InterceptorFunc.FAIL(response, "{\"code\":\"404\",\"msg\":\"需要先登录\",\"data\":null}");			
				}
			}
		}
		*/

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
