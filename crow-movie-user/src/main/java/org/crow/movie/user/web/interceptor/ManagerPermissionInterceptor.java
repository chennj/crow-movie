package org.crow.movie.user.web.interceptor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crow.movie.user.web.annotation.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class ManagerPermissionInterceptor extends HandlerInterceptorAdapter{

	protected static final Logger logger = LoggerFactory.getLogger(ManagerPermissionInterceptor.class.getClass());
	
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

		if (!(handler instanceof HandlerMethod)) {
			return super.preHandle(request, response, handler);
		}

		// 如果是manager功能，但地址不在ip白名单，拒绝访问
		HandlerMethod method = (HandlerMethod)handler;
		Permission permission = method.getMethodAnnotation(Permission.class);
		if (permission == null || permission.managerLimit()){
			
			boolean goAHead 	= false;
			boolean ajaxFlag 	= "XMLHttpRequest".equalsIgnoreCase(request.getHeader("x-requested-with"));
			String referer 		= request.getHeader("Referer");
			if (referer == null){
				if (!"yes".equalsIgnoreCase(allowEmpty)){
					if (ajaxFlag){
						response.setContentType("application/json);charset=utf-8");
					}
					PrintWriter writer = null;
					try{
						writer = response.getWriter();
						writer.write("{\"code\":\"404\",\"msg\":\"不能在地址栏直接访问\"}");
						writer.flush();
					} catch (Exception e){
						logger.info("ManagerPermission.preHandle>>>"+e.getMessage());
					} finally{
						try{
							if (null != writer){
								writer.close();
							}
						} catch(Exception e){}
					}
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
				
				PrintWriter writer = null;
				try{
					writer = response.getWriter();
					writer.write("{\"code\":\"404\",\"msg\":\"访问地址不在白名单内\"}");
					writer.flush();
				} catch (Exception e){
					logger.info("ManagerPermission.preHandle>>>"+e.getMessage());
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
		
		return true;
	}
}
