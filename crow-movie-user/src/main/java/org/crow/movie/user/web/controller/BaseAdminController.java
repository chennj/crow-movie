package org.crow.movie.user.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crow.movie.user.common.db.entity.AdminInfo;
import org.crow.movie.user.common.db.service.AdminInfoService;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.common.util.TokenUtil;
import org.crow.movie.user.web.interceptor.InterceptorFunc;
import org.crow.movie.user.web.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseAdminController extends BaseController{
	
	@Autowired
	private AdminInfoService adminInfoService;
		
	/**
	 * 用于根据token获取admin用户
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	@ModelAttribute
	protected AdminInfo getAdminInfo(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String token = request.getHeader("accessToken");
		if (StrUtil.isEmpty(token)){
			if (InterceptorFunc.IsUrlWhiteList(request, response, TokenInterceptor.excludeUrls)){
				return null;
			} else {
				response.sendError(HttpServletResponse.SC_FORBIDDEN,"非法访问");
				return null;
			}
		}
		Integer id = TokenUtil.getUserID(token);
		AdminInfo admin = adminInfoService.getById(id);
		if (null == admin){
			response.sendError(HttpServletResponse.SC_FORBIDDEN,"非法访问");
			return null;
		} else {
			return admin;
		}
	}
	
	protected int getAdminId(HttpServletRequest request){
		
		String token = request.getHeader("accessToken");
		return TokenUtil.getUserID(token);
	}
	
}
