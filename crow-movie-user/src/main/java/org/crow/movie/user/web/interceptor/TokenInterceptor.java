package org.crow.movie.user.web.interceptor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crow.movie.user.common.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class TokenInterceptor extends HandlerInterceptorAdapter{

	protected static final Logger logger = LoggerFactory.getLogger(TokenInterceptor.class.getClass());
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		response.setCharacterEncoding("utf-8");
		String token = request.getHeader("accessToken");
		
		if (null != token){
			//验证token是否正确
			boolean result = TokenUtil.tokenVerify(token);
			if (result){
				return true;
			}
		}
		
		PrintWriter writer = null;
		try{
			writer = response.getWriter();
			writer.write("{\"code\":\"404\",\"msg\":\"先获取token\",\"data\":null}");
			writer.flush();
		} catch (Exception e){
			logger.info("TokenInterceptor.preHandle>>>"+e.getMessage());
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
