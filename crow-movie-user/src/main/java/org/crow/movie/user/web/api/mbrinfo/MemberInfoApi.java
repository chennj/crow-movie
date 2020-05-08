package org.crow.movie.user.web.api.mbrinfo;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.db.entity.AppLevel;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.AppLevelService;
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
import com.alibaba.fastjson.TypeReference;

@Controller
@RequestMapping("/mbrinfo")
public class MemberInfoApi  extends BaseController{

	@Autowired
	MemberInfoService memberInfoService;
	
	@Autowired
	AppLevelService appLevelService;
	
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
	
	@RequestMapping(value="add", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<?> add(
			HttpServletRequest request,
			@RequestParam(required=true) String data){
		
		logger.info("mbrinfo.add>>>enter,recive data="+data);
		
		// param
		if (StrUtil.isEmpty(data)){
			logger.error("mbrinfo.add>>>数据的没有，搞啥");
			return fail("data is empty");
		}
		
		JSONObject jdata = null;
		MemberInfo mbr = null;
		String account;
		String password,password2;
		try {
			jdata 		= JSON.parseObject(data);
			account 	= jdata.getString("account");
			password 	= jdata.getString("password");
			password2 	= jdata.getString("password2");
			if (StrUtil.isEmpty(account) || StrUtil.isEmpty(password) || StrUtil.isEmpty(password2)){
				return fail("请输入名称，密码");
			}
			if (!password2.endsWith(password)){
				return fail("两次密码不一致");
			}
		} catch (Exception e){
			logger.error("mbrinfo.add>>>"+e.getMessage());
			return new ReturnT<String>(500, "data trans json error");
		}
		
		mbr = memberInfoService.getUnique("account", account);
		if (null != mbr){
			return new ReturnT<String>(500, "user exist");
		}
		
		mbr = JSON.parseObject(data, new TypeReference<MemberInfo>(){});
		if (null == mbr){
			logger.error("mbrinfo.add>>>data trans bean exception");
			return fail("data trans bean exception");
		}
		
		String pwd = DigestUtils.encryptMd5(DigestUtils.encryptMd5(password)+salt);
		Long ts = System.currentTimeMillis(); 
		mbr.setNickName(account);
		mbr.setIsVisitor(2);
		mbr.setPassword(pwd);
		mbr.setCreateDate(new Date());
		mbr.setCreateTime(SomeUtil.safeLongToInt(ts));
		mbr.setUpdateTime(SomeUtil.safeLongToInt(ts));

		memberInfoService.add(mbr);
		
		return success();
		
	}
	
	@RequestMapping(value="del", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<?> del(
			HttpServletRequest request,
			@RequestParam(required=true) Integer id){
		
		if (StrUtil.isEmpty(id)){
			return fail("id is empty");
		}
		
		MemberInfo entity = memberInfoService.getById(id);
		if (null == entity){
			return fail("member is not exists");
		}

		memberInfoService.delete(id);
		
		return success("delete complete");
	}
	
	@RequestMapping(value="status", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<?> status(
			HttpServletRequest request,
			@RequestParam(required=true) Integer id){
	
		if (StrUtil.isEmpty(id)){
			return fail("id is empty");
		}

		MemberInfo entity = memberInfoService.getById(id);
		if (null == entity){
			return fail("用户不存在");
		}
		
		int n = memberInfoService.updateStatus(id);
		if (n>0)return success();
		else return fail();
	}
	
	@RequestMapping(value="details", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<?> details(
			HttpServletRequest request,
			@RequestParam(required=true) Integer id){
	
		if (StrUtil.isEmpty(id)){
			return fail("id is empty");
		}

		MemberInfo entity = memberInfoService.getById(id);
		if (null == entity){
			return fail("用户不存在");
		}
		
		List<AppLevel> list = appLevelService.getList("status", 1);
		
		JSONObject jRet = new JSONObject(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				this.put("res", entity);
				this.put("class_list", list);
			}
		};
		
		return success(jRet);
		
	}
}
