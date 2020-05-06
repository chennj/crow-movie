package org.crow.movie.user.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class SecurityInterceptor extends HandlerInterceptorAdapter{

	/**
	 * ip白名单
	 */
	@Value("${movie.user.addrUrl}")
	private String addrUrl;
	
	/**
	 * referer 是否允许空
	 */
	@Value("${movie.user.allowRefererEmpty}")
	private String allowEmpty;
	
	private static String[] addrUrls;

	/**
	 * 地址白名单
	 */
	@Value("${movie.user.excludeUrl}")
	private String excludeUrl;
	
	private static String[] excludeUrls;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		if (null == addrUrls){
			addrUrls = addrUrl.split(";");
		}
		
		if (null == excludeUrls){
			excludeUrls = excludeUrl.split(",");
		}
		
		request.setCharacterEncoding("UTF-8");
		String contextName 	= request.getContextPath();
		String url 			= request.getRequestURI().replace(contextName, "");	
		
		// 在地址白名单中不进行拦截(例如isAlive)
		for (int i = 0; i < excludeUrls.length; i++) {
			String excludeUrl = excludeUrls[i];
			if (url.startsWith(excludeUrl)) {
				return true;
			}
		}

		// 如果不在ip白名单，拒绝访问
		boolean goAHead = false;
		boolean ajaxFlag = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("x-requested-with"));
		String referer = request.getHeader("Referer");
		if (referer == null){
			if (!"yes".equalsIgnoreCase(allowEmpty)){
				if (ajaxFlag){
					response.setContentType("application/json);charset=utf-8");
				}
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "referer can't empty");
				return false;
			}
			return true;
		}
		for (int i=0; i < addrUrls.length; i++){
			String addr = addrUrls[i];
			if (referer.startsWith(addr)){
				return true;
			}
		}		
		if (!goAHead){
			if (ajaxFlag){
				response.setContentType("application/json);charset=utf-8");
			}
			response.sendError(HttpServletResponse.SC_FORBIDDEN, referer+" have not right access");
			return false;
		}
		
		return true;
	}
}
