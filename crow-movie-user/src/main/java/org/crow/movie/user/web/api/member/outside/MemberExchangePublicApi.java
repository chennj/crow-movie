package org.crow.movie.user.web.api.member.outside;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.db.entity.AppVip;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.AppExchangeService;
import org.crow.movie.user.common.db.service.AppVipService;
import org.crow.movie.user.common.db.service.MemberExchangeService;
import org.crow.movie.user.common.util.RegexUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.web.annotation.Permission;
import org.crow.movie.user.web.controller.BasePublicController;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/public/mbrexchange")
@Permission(managerLimit=false)
@Api(tags = "Exchange Related Interface Of Mobile Endpoint User",description="手机端用户兑换相关接口,需要token")
public class MemberExchangePublicApi extends BasePublicController{

	@Autowired
	private MemberExchangeService memberExchangeService;
	
	@Autowired
	private AppVipService appVipService;
	
	@Autowired
	private AppExchangeService appExchangeService;
	
	/**
	 * 兑换
	 * @param request
	 * @param allParams
	 * @return
	 */
	@ApiOperation(value = "兑换",notes="兑换")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="deviceid",value="设备ID",required=true,paramType="header"),
		@ApiImplicitParam(name="code",value="激活码",required=true,paramType="query"),
		@ApiImplicitParam(name="verify_code",value="验证码",required=true,paramType="query")
	})
	@RequestMapping(value="exchange", method=RequestMethod.POST)
	public ReturnT<?> exchange(HttpServletRequest request,
			@RequestParam(required=true)String code,
			@RequestParam(required=true)String verify_code){

		logger.info("public.mbrexchange.search>>>enter,recive data="+code+","+verify_code);
		
		String deviceid = this.getDid(request);
		if (StrUtil.isEmpty(deviceid)){
			return fail("需要设备ID");
		}
		
		if (StrUtil.isEmpty(code)){
			return fail("激活码不能为空");
		}
		if (code.length() != 12){
			return fail("激活码长度有误");
		}
		if (RegexUtil.isNotNumOrChar(code)){
			return fail("激活码只能是数字、字母");
		}
		
		if (StrUtil.isEmpty(verify_code)){
			return fail("验证码不能为空");
		}
		if (RegexUtil.isNotNumOrChar(verify_code)){
			return fail("验证码只能是数字，字母");
		}
		if (verify_code.length()<1 || verify_code.length()>10){
			return fail("验证码长度有误");
		}
		if (!this.checkRandomCode(deviceid, verify_code)){
			return fail("验证码不正确");
		}
		
		Map<String, Object> eq = new HashMap<>();
		eq.put("code", code);
		eq.put("status", 1);
		AppVip vip = appVipService.getUnique(eq);
		
		Map<String, Object> exchgMap = appExchangeService.unique(code);
		
		if (null == exchgMap){
			return fail("激活码有误");
		}
		
		logger.info("exchgMap data:"+exchgMap.entrySet());
		
		JSONObject juser = this.getJUser();
		if (null == juser){
			return fail("数据异常");
		}
		
		MemberInfo user = this.getUser();
		try {
			memberExchangeService.exchange(vip, exchgMap,juser, user);		
		} catch (RuntimeException e){
			return fail(e.getMessage());
		}
		return success();
	}
	
	@ApiOperation(value = "删除",notes="删除")
	@ApiImplicitParams({
	})
	@RequestMapping(value="del", method=RequestMethod.POST)
	public ReturnT<?> del(@RequestParam(required = true) Integer id){
		
		if (StrUtil.isEmpty(id)){
			return fail("没有id");
		}
		
		memberExchangeService.del(id);
		
		return success("操作完成");
	}
}
