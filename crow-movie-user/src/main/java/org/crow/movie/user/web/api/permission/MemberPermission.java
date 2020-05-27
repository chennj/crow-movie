package org.crow.movie.user.web.api.permission;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.util.DigestUtils;
import org.crow.movie.user.common.util.SomeUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.web.annotation.Permission;
import org.crow.movie.user.web.controller.BaseController;
import org.crow.movie.user.web.interceptor.MemberPermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

@Controller
@RequestMapping("/")
@Permission(memberLimit=false,managerLimit=false)
public class MemberPermission extends BaseController{

	@Autowired
	MemberInfoService memberInfoService;
	
	
	@RequestMapping("isAlive")
    @ResponseBody
    public String isAlive() {
		
		//Page<MemberInfo> page = memberInfoService.page(1, 10, null);
		//return page.toJsonString();
		return "isAlive";
    }
	
	@RequestMapping(value="register", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<String> register(
			HttpServletRequest request, 
			HttpServletResponse response,
			@RequestParam Map<String,Object> allParams){
		
		logger.info("register>>>接收到的数据："+allParams.entrySet());		

		
		JSONObject jdata = null;
		MemberInfo mbr = null;
		String account;
		String password;
		try {
			jdata 		= new JSONObject(allParams);
			account 	= jdata.getString("account");
			password 	= jdata.getString("password");
			if (StrUtil.isEmpty(account) || StrUtil.isEmpty(password)){
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
		
		mbr = JSON.parseObject(jdata.toJSONString(), new TypeReference<MemberInfo>(){});
		if (null == mbr){
			logger.error("register>>>data trans bean exception");
			return new ReturnT<String>(500, "data trans bean exception");
		}
		
		String pwd = DigestUtils.encryptPwd(DigestUtils.encryptMd5(password));
		Long ts = System.currentTimeMillis(); 
		mbr.setPassword(pwd);
		mbr.setCreateTime(SomeUtil.safeLongToInt(ts));
		mbr.setUpdateTime(SomeUtil.safeLongToInt(ts));

		memberInfoService.add(mbr);
		
		return ReturnT.SUCCESS;
	}
	
	@RequestMapping(value="login", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<String> loginDo(HttpServletRequest request, HttpServletResponse response, String account, String password, String ifRemember){
		
		// valid
		if (MemberPermissionInterceptor.ifMemberLogin(request)) {
			return success("登录还在有效期");
		}

		// param
		if (StrUtil.isEmpty(account) || StrUtil.isEmpty(password)){
			return fail("请输入账号密码");
		}
		boolean ifRem = (ifRemember!=null && "on".equals(ifRemember))?true:false;

		// do login
		boolean loginRet = MemberPermissionInterceptor.login(response, request, account, password, ifRem);
		if (!loginRet) {
			return fail("账号密码错误");
		}
		return success();
	}

	@RequestMapping(value="logout", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response){
		if (MemberPermissionInterceptor.ifMemberLogin(request)) {
			MemberPermissionInterceptor.logout(request, response);
		}
		return success();
	}


}
