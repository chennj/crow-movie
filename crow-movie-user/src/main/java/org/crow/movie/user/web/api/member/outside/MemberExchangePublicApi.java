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
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.db.service.MemberVipService;
import org.crow.movie.user.common.util.RegexUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.web.controller.BasePublicController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

@RestController
@RequestMapping("/public/mbrexchange")
public class MemberExchangePublicApi extends BasePublicController{

	@Autowired
	MemberExchangeService memberExchangeService;
	
	@Autowired
	MemberVipService memberVipService;
	
	@Autowired
	MemberInfoService memberInfoService;
	
	@Autowired
	AppVipService appVipService;
	
	@Autowired
	AppExchangeService appExchangeService;
	
	/**
	 * 兑换
	 * @param request
	 * @param allParams
	 * @return
	 */
	@RequestMapping(value="exchange", method=RequestMethod.POST)
	public ReturnT<?> exchange(HttpServletRequest request,
			@RequestParam(required=true)String code,
			@RequestParam(required=true)String verify_code){

		logger.info("public.mbrexchange.search>>>enter,recive data="+code+","+verify_code);
		
		if (StrUtil.isEmpty(code)){
			return fail("激活码不能为空");
		}
		if (code.length() != 12){
			return fail("激活码长度有误");
		}
		if (RegexUtil.isNotNumOrChar(code)){
			return fail("激活码只能是数字、字母");
		}
		
		/* 暂时不检查验证码
		if (StrUtil.isEmpty(verify_code)){
			return fail("验证码不能为空");
		}
		if (RegexUtil.isNotNum(verify_code)){
			return fail("验证码只能是数字");
		}
		if (verify_code.length()<1 || verify_code.length()>10){
			return fail("验证码长度有误");
		}
		*/
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
	
	@RequestMapping(value="del", method=RequestMethod.POST)
	public ReturnT<?> del(@RequestParam(required = true) Integer id){
		
		if (StrUtil.isEmpty(id)){
			return fail("没有id");
		}
		
		memberExchangeService.del(id);
		
		return success("操作完成");
	}
}
