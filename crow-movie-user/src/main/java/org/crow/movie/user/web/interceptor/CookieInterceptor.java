package org.crow.movie.user.web.interceptor;

import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 将cookie map放入model传递给前端
 * @author chenn
 *
 */
@Component
public class CookieInterceptor extends HandlerInterceptorAdapter {

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		if (	modelAndView != null 
				&& request.getCookies() != null
				&& request.getCookies().length > 0){
			
			HashMap<String, Cookie> cookieMap = new HashMap<>();
			
			for (Cookie one : request.getCookies()){
				cookieMap.put(one.getName(), one);
			}
			
			modelAndView.addObject("cookieMap", cookieMap);
		}
		
		super.postHandle(request, response, handler, modelAndView);
	}

	
}
