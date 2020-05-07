package org.crow.movie.user.web.api.mbrinfo;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.util.DigestUtils;
import org.crow.movie.user.common.util.SomeUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/mbrinfo")
public class MemberInfoApi  extends BaseController{

	@Autowired
	MemberInfoService memberInfoService;
	
	@RequestMapping(value="changpwd", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<?> changPwd(
			HttpServletRequest request,
			@RequestParam(required = true) String password,
			@RequestParam(required = true) String password2){
		
		// param
		if (StrUtil.isEmpty(password) || StrUtil.isEmpty(password2)){
			return fail("account or pwd is empty");
		}
		if (!password.equals(password2)){
			return fail("twice password is not same");
		}
		
		MemberInfo mbr = memberInfoService.getById(getMemberInfo(request).getId());
		if (null == mbr){
			return fail("user is not exists");
		}
		
		String pwd = DigestUtils.encryptMd5(DigestUtils.encryptMd5(password)+salt);
		mbr.setPassword(pwd);
		mbr.setCreateIp(getIp(request));
		mbr = memberInfoService.modify(mbr);
		
		return success(mbr);
	}
	
	@RequestMapping(value="edit", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<?> edit(
			HttpServletRequest request,
			@RequestParam(required=true) String data){
		
		logger.info("mbrinfo.edit>>>enter,recive data="+data);
		
		// param
		if (StrUtil.isEmpty(data)){
			return fail("data is empty");
		}
				
		JSONObject jo = null;
		try {
			jo = JSON.parseObject(data);
		} catch (Exception e){
			logger.error("mbrinfo.edit>>>"+e.getMessage());
			return fail(e.getMessage());
		}
		
		MemberInfo entity = memberInfoService.getById(getMemberInfo(request).getId());
		if (null == entity){
			return fail("member is not exists");
		}
		
		try {
			SomeUtil.updateBean(entity, jo);
		} catch (Exception e) {
			logger.error("mbrinfo.edit>>>updateBean failed,"+e.getMessage());
			return fail("modify user info failed");
		}
		
		memberInfoService.modify(entity);
		
		return success();
	}
}
