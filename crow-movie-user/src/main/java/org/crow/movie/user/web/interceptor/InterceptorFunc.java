package org.crow.movie.user.web.interceptor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crow.movie.user.common.constant.Const;
import org.crow.movie.user.common.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InterceptorFunc {

	protected static final Logger logger = LoggerFactory.getLogger(InterceptorFunc.class.getClass());
	
	protected static String dir;
	static {
		dir = System.getProperty("user.dir");
		dir = dir.substring(0,dir.lastIndexOf(File.separator));
	}
	
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
	
	public static String getUrlWhiteList() throws IOException{

		String excludeUrl = PropertyUtil.instance()
				.getValueByDefaultFileKey(dir,Const.CONFIG_COMMON_FILE, Const.EXCLUDE_URL);
		return excludeUrl;

	}
}
