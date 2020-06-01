package org.crow.movie.user.web.api.member.pc;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.entity.MemberOpen;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.db.service.MemberOpenService;
import org.crow.movie.user.common.util.DigestUtils;
import org.crow.movie.user.common.util.IPUtil;
import org.crow.movie.user.common.util.RegexUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.common.util.TokenUtil;
import org.crow.movie.user.web.controller.BasePcController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

@RestController
@RequestMapping(value="pc")
public class MemberPcApi extends BasePcController{

	@Autowired
	private MemberInfoService memberInfoService;
	
	@Autowired
	private MemberOpenService memberOpenService;
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value="login-account", method=RequestMethod.POST)
	public ReturnT<?> loginAccount(
			HttpServletRequest request, 
			HttpServletResponse response, 
			@RequestParam(required=true)String account, 
			@RequestParam(required=true)String password){
		
		if (StrUtil.isEmpty(account)){
			return fail("账号不能为空");
		}
		if (account.length()<6 || account.length()>15){
			return fail("账号长度有误");
		}
		if (RegexUtil.isNotNumOrChar(account)){
			return fail("账号只能是数字、字母");
		}

		if (StrUtil.isEmpty(password)){
			return fail("密码不能为空");
		}
		if (password.length()<6 || password.length()>15){
			return fail("密码长度有误");
		}
		if (RegexUtil.isNotNumOrChar(password)){
			return fail("密码只能是数字、字母");
		}
		
		MemberInfo member = memberInfoService.getUnique("account", account);
		if (null == member){
			return fail("用户不存在");
		}
		try {
			if (!password.equals(DigestUtils.decryptPwd(member.getPassword()))){
				return fail("账号或密码错误");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return fail(e.getMessage());
		}
		
		if (member.getStatus() == 1){
			return fail("用户已禁止登录");
		}
		if (member.getIsVisitor() == 1){
			return fail("游客禁止登录");
		}
		
		member.setToken(DigestUtils.random(32));
		member.setUpdateTime(now());
		member.setUpdateTime(now());
		memberInfoService.modify(member);
		
		MemberOpen memberOpen = new MemberOpen();
		memberOpen.setMemberId(member.getId());
		memberOpen.setDeviceId(null);
		memberOpen.setDeviceType("PC");
		memberOpen.setCreateIp(IPUtil.getClientIp(request));
		memberOpen.setCreateTime(now());
		try {
			memberOpen.setCreateDate(new SimpleDateFormat().parse("yyyy-MM-dd"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		memberOpenService.add(memberOpen);
		
		JSONObject jRet = new JSONObject();
		jRet.put("user_id", member.getId());
		jRet.put("accessToken", TokenUtil.genToken(member.getAccount(), member.getId()));
		return success(jRet);
	}
	

}
