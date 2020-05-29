package org.crow.movie.user.web.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crow.movie.user.common.constant.Const;
import org.crow.movie.user.common.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class TokenInterceptor extends HandlerInterceptorAdapter implements InitializingBean {

	protected static final Logger logger = LoggerFactory.getLogger(TokenInterceptor.class.getClass());
	
	private static String[] excludeUrls;
	
	@Override
	public void afterPropertiesSet() throws Exception {

		try {
			String excludeUrl = InterceptorFunc.getUrlWhiteList();
			excludeUrls = excludeUrl.split(",");
		} catch (IOException e){
			throw new Exception( Const.CONFIG_COMMON_FILE + " 没有找到！");
		}

	}
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		if (!(handler instanceof HandlerMethod)) {
			return super.preHandle(request, response, handler);
		}

		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("UTF-8");
				
		if (InterceptorFunc.IsUrlWhiteList(request, response, excludeUrls)){
			return true;
		}

		String token = request.getHeader("accessToken");
		
		if (null != token){
			//验证token是否正确
			boolean result = TokenUtil.tokenVerify(token);
			if (result){
				return true;
			} else {
				return InterceptorFunc.FAIL(response, "{\"code\":\"404\",\"msg\":\"token无效\",\"data\":null}");
			}
		}
		
		return InterceptorFunc.FAIL(response, "{\"code\":\"404\",\"msg\":\"先获取token\",\"data\":null}");

	}

	
}
