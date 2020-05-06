package org.crow.movie.user.web.interceptor;

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
import org.crow.movie.user.web.annotation.PermessionLimit;
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
public class PermissionInterceptor extends HandlerInterceptorAdapter implements InitializingBean {

	/**
	 * 密码加盐
	 */
	@Value("${movie.user.salt}")
	private String salt;
	
	private static String pwdSalt;
	
	/**
	 * 地址白名单
	 */
	@Value("${acti.time.excludeUrl}")
	private String excludeUrl;
	
	private static String[] excludeUrls;
	
	private static MemberInfoService memberInfoService;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		if (	excludeUrl == null 
				|| excludeUrl.trim().length() == 0
				|| salt == null
				|| salt.trim().length() == 0){
			throw new RuntimeException("Address White List And Salt Cann't Be Empty! In Application Property File");
		}
		
		pwdSalt = salt;
		
		excludeUrls = excludeUrl.split(",");
		
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		HttpServletRequest req = (HttpServletRequest) request;
		//HttpServletResponse resp = (HttpServletResponse) response;
		//HttpSession session = req.getSession();
		
		String contextName = req.getContextPath();
		String url = req.getRequestURI();
		url = url.replace(contextName, "");
				
		// 在地址白名单中不进行拦截(例如isAlive)
		for (int i = 0; i < excludeUrls.length; i++) {
			String excludeUrl = excludeUrls[i];
			if (url.startsWith(excludeUrl)) {
				return true;
			}
		}
		
		if (!(handler instanceof HandlerMethod)) {
			return super.preHandle(request, response, handler);
		}

		if (!ifLogin(request)) {
			HandlerMethod method = (HandlerMethod)handler;
			PermessionLimit permission = method.getMethodAnnotation(PermessionLimit.class);
			if (permission == null || permission.limit()) {
				response.sendRedirect(request.getContextPath() + "/toLogin");
				//request.getRequestDispatcher("/toLogin").forward(request, response);
				return false;
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
		
		MemberInfo userInfo = memberInfoService.getUnique("no", username);
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
	
	public static boolean ifLogin(HttpServletRequest request){
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
