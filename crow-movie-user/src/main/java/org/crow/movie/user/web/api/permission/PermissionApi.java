package org.crow.movie.user.web.api.permission;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crow.movie.user.common.db.Page;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.util.DigestUtils;
import org.crow.movie.user.common.util.SomeUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.web.annotation.PermessionLimit;
import org.crow.movie.user.web.controller.BaseController;
import org.crow.movie.user.web.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

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
	public ReturnT<String> register(HttpServletRequest request, HttpServletResponse response){
		
		String js = request.getParameter("data");
		
		logger.info("register>>>接收到的数据："+js);		
		
		// param
		if (js==null || js.trim().length()==0){
			logger.error("register>>>数据的没有，搞啥");
			return new ReturnT<String>(500, "data is empty");
		}
		
		JSONObject jdata = null;
		MemberInfo mbr = null;
		String account;
		String password;
		try {
			jdata 		= JSON.parseObject(js);
			account 	= jdata.getString("account");
			password 	= jdata.getString("password");
			if (StrUtil.isNEmpty(account) || StrUtil.isNEmpty(password)){
				return new ReturnT<String>(500, "name or pwd is empty");
			}
		} catch (Exception e){
			logger.error("register>>>"+e.getMessage());
			return new ReturnT<String>(500, "data trans json error");
		}
		
		mbr = memberInfoService.getUnique("account", account);
		if (null != mbr){
			return new ReturnT<String>(500, "user exist");
		}
		
		mbr = JSON.parseObject(js, new TypeReference<MemberInfo>(){});
		if (null == mbr){
			logger.error("register>>>data trans bean exception");
			return new ReturnT<String>(500, "data trans bean exception");
		}
		
		String pwd = DigestUtils.encryptMd5(DigestUtils.encryptMd5(password)+salt);
		Long ts = System.currentTimeMillis(); 
		mbr.setPassword(pwd);
		mbr.setCreateTime(SomeUtil.safeLongToInt(ts));
		mbr.setUpdateTime(SomeUtil.safeLongToInt(ts));

		memberInfoService.add(mbr);
		
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
