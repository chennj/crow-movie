package org.crow.movie.user.web.interceptor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InterceptorFunc {

	protected static final Logger logger = LoggerFactory.getLogger(InterceptorFunc.class.getClass());
	
	public static boolean FAIL(HttpServletResponse response,String message){
		
		PrintWriter writer = null;
		try{
			writer = response.getWriter();
			writer.write(message);
			writer.flush();
			logger.error("InterceptorFunc.preHandle>>>"+message);
		} catch (Exception e){
			logger.error("InterceptorFunc.preHandle>>>"+e.getMessage());
		} finally{
			try{
				if (null != writer){
					writer.close();
				}
			} catch(Exception e){}
		}
		
		return false;
	}
	
	public static boolean IsUrlWhiteList(HttpServletRequest request, HttpServletResponse response, String[] excludeUrls){
		
		String contextName 		= request.getContextPath();
		String requestMethod 	= request.getRequestURI().replace(contextName, "");	
		
		for (int i = 0; i < excludeUrls.length; i++) {
			String excludeUrl = excludeUrls[i];
			if (requestMethod.startsWith(excludeUrl)) {
				return true;
			}
		}
		return false;
	}
}
