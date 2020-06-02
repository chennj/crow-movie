package org.crow.movie.user.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.db.entity.AdminInfo;
import org.crow.movie.user.common.db.service.AdminInfoService;
import org.crow.movie.user.common.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseAdminController extends BaseController{
	
	@Autowired
	private AdminInfoService adminInfoService;
		
	/**
	 * 用于根据token获取admin用户
	 * @param request
	 * @return
	 */
	protected AdminInfo getAdminInfo(HttpServletRequest request){
		String token = request.getHeader("accessToken");
		Integer id = TokenUtil.getUserID(token);
		return adminInfoService.getById(id);
	}
	
	protected int getAdminId(HttpServletRequest request){
		
		String token = request.getHeader("accessToken");
		return TokenUtil.getUserID(token);
	}
	
}
