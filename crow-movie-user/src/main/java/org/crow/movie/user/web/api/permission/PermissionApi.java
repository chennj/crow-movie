package org.crow.movie.user.web.api.permission;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crow.movie.user.common.db.Page;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.web.annotation.PermessionLimit;
import org.crow.movie.user.web.controller.BaseController;
import org.crow.movie.user.web.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/")
public class PermissionApi extends BaseController{

	@Autowired
	MemberInfoService memberInfoService;
	
	
	@RequestMapping("demo")
    @ResponseBody
    @PermessionLimit(limit=false)
    public String demo() {
		
		Page<MemberInfo> page = memberInfoService.page(1, 10, null);
		return page.toJsonString();
    }
	
	@RequestMapping(value="register", method=RequestMethod.POST)
	@ResponseBody
	@PermessionLimit(limit=false)
	public ReturnT<String> register(HttpServletRequest request, HttpServletResponse response, String userName, String password){
		
		// valid
		if (PermissionInterceptor.ifLogin(request)) {
			return ReturnT.SUCCESS;
		}

		// param
		if (userName==null || userName.trim().length()==0 || password==null || password.trim().length()==0){
			return new ReturnT<String>(500, "请输入账号密码");
		}
		boolean ifRem = false;

		// do login
		boolean loginRet = PermissionInterceptor.login(response, request, userName, password, ifRem);
		if (!loginRet) {
			return new ReturnT<String>(500, "账号密码错误");
		}
		return ReturnT.SUCCESS;
	}
	
	@RequestMapping(value="login", method=RequestMethod.POST)
	@ResponseBody
	@PermessionLimit(limit=false)
	public ReturnT<String> loginDo(HttpServletRequest request, HttpServletResponse response, String userName, String password, String ifRemember){
		
		// valid
		if (PermissionInterceptor.ifLogin(request)) {
			return ReturnT.SUCCESS;
		}

		// param
		if (userName==null || userName.trim().length()==0 || password==null || password.trim().length()==0){
			return new ReturnT<String>(500, "请输入账号密码");
		}
		boolean ifRem = (ifRemember!=null && "on".equals(ifRemember))?true:false;

		// do login
		boolean loginRet = PermissionInterceptor.login(response, request, userName, password, ifRem);
		if (!loginRet) {
			return new ReturnT<String>(500, "账号密码错误");
		}
		return ReturnT.SUCCESS;
	}

	@RequestMapping(value="logout", method=RequestMethod.POST)
	@ResponseBody
	@PermessionLimit(limit=false)
	public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response){
		if (PermissionInterceptor.ifLogin(request)) {
			PermissionInterceptor.logout(request, response);
		}
		return ReturnT.SUCCESS;
	}


}
