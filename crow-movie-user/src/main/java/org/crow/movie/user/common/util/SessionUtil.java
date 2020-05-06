package org.crow.movie.user.common.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SessionUtil {

	public static HttpSession getSession(){
		 //获取到ServletRequestAttributes 里面有 
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //获取到Request对象
        HttpServletRequest request = attrs.getRequest();
        //获取到Session对象
        HttpSession session = request.getSession();
        
        return session;
	}
	
	public static void setSession(String key, Object value){
		
		HttpSession session = getSession();
		
		session.setAttribute(key, value);
		
	}
	
	public static Object getSession(String key){
		
		return getSession().getAttribute(key);
	}
}
