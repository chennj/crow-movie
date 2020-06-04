package org.crow.movie.user.web.api.permission;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crow.movie.user.common.db.entity.AdminInfo;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.AdminInfoService;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.util.DigestUtils;
import org.crow.movie.user.common.util.TokenUtil;
import org.crow.movie.user.web.annotation.Permission;
import org.crow.movie.user.web.controller.BaseAdminController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("/token")
@Permission(memberLimit=false,managerLimit=false)
@Api(tags = "token获取接口",description="其他接口的前置接口")
public class TokenPermission extends BaseAdminController{

	/**
	 * 密码加盐
	 */
	@Value("${movie.user.salt}")
	private String salt;

	@Autowired
	private MemberInfoService memberInfoService;
	
	@Autowired
	private AdminInfoService adminInfoService;
	
	@ApiOperation(value = "管理员token获取接口",notes="后台管理员获取token的接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name="username",value="管理员账号",required=true,paramType="query"),
		@ApiImplicitParam(name="password",value="密码",required=true,paramType="query")
		})
	@RequestMapping(value="/private", method=RequestMethod.POST)
	public ReturnT<?> getToken(
			HttpServletRequest request, 
			HttpServletResponse response,
			@RequestParam(required = true) String username,
			@RequestParam(required = true) String password){
		
		logger.info("/token/get>>>用户："+username);		
		
		AdminInfo admin = adminInfoService.getUnique("username", username);
		if (null == admin){
			return fail("用户不存在");
		}
		
		String pwd;
		try {
			pwd = DigestUtils.decryptPwd(admin.getPassword());
		} catch (UnsupportedEncodingException e) {
			logger.error("/token/private>>>解码失败:"+e.getMessage());
			return fail("解码失败");
		}
		if (!password.endsWith(pwd)){
			return fail("密码错误");
		}
		
		JSONObject jRet = new JSONObject();
		jRet.put("user_id", admin.getId());
		jRet.put("accessToken", TokenUtil.genToken(admin.getUsername(), admin.getId()));
		return success(jRet);
	}

	@ApiOperation(value = "客户token获取接口",notes="后台管理员获取token的接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name="account",value="账号",defaultValue="Km1688",required=true,paramType="query"),
		@ApiImplicitParam(name="password",value="密码",defaultValue="qqqqqq",required=true,paramType="query")
		})
	@RequestMapping(value="/public", method=RequestMethod.POST)
	public ReturnT<?> getPublicToken(
			HttpServletRequest request,
			@RequestParam(required = true) String account,
			@RequestParam(required = true) String password){
		
		//String deviceid = request.getHeader("deviceid");
		//if (StrUtil.isEmpty(deviceid)){
		//	return fail("请求头缺少设备ID");
		//}
		
		MemberInfo userInfo = memberInfoService.getUnique("account", account);
		if (null == userInfo){
			return fail("用户不存在");
		}
		
		//if (!deviceid.equals(userInfo.getDeviceId())){
		//	return fail("请求设备与预留设备不一致");
		//}
		
		//String pwd = DigestUtils.encryptMd5(DigestUtils.encryptMd5(password)+salt);
		//if (!userInfo.getPassword().equals(pwd)){
		//	return fail("密码错误");
		//}
		
		//SessionUtil.setSession(Const.SESSION_USER_INFO_KEY, userInfo);

		//for debug
		String pwd;
		try {
			pwd = DigestUtils.decryptPwd(userInfo.getPassword());
		} catch (UnsupportedEncodingException e) {
			logger.error("/token/private>>>解码失败:"+e.getMessage());
			return fail("解码失败");
		}
		if (!password.equals(pwd)){
			return fail("密码错误");
		}
		
		JSONObject jRet = new JSONObject();
		jRet.put("user_id", userInfo.getId());
		jRet.put("accessToken", TokenUtil.genToken(userInfo.getAccount(), userInfo.getId()));
		return success(jRet);
	}
}
