package org.crow.movie.user.web.api.member.pc;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crow.movie.user.common.ApplicationProperties;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.entity.MemberOpen;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.db.service.MemberOpenService;
import org.crow.movie.user.common.plugin.qrcode.QRCodeService;
import org.crow.movie.user.common.util.CommUtil;
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
	
	@Autowired
    private QRCodeService qrCodeService;
	
	@Autowired
    private ApplicationProperties appProperties;
	
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
		
		if (StrUtil.isEmpty(member.getPromoQrcode())){
			
			int width 	= 166;
			int height 	= 166;
			
			String qrcodeDir = appProperties.getQrcodeDir();
			String uploadDir = appProperties.getUploadDir();
			String promo_url = CommUtil.getDomain()+"?code=";
	        
			String data = promo_url+member.getPromoCode();
			String imageName = String.format("%d_qr.png", member.getId());
	        File qrFile = new File(qrcodeDir, imageName);
	        qrCodeService.encode(data, width, height, qrFile);
	        
	        logger.info("生成的二维码文件:{}", qrFile.getAbsolutePath());
	        
	        File logoFile = new File(uploadDir, "logo.png");
	        imageName = String.format("%s_qr_logo.png", member.getId());
	        File logoQrFile = new File(qrcodeDir, imageName);
	        qrCodeService.encodeWithLogo(qrFile, logoFile, logoQrFile);
	        logger.info("生成的带logo二维码文件:{}", logoQrFile.getAbsolutePath());
	        
	        String promo_filename = logoQrFile.getName();
	        member.setPromoQrcode(promo_filename);
	        memberInfoService.modify(member);

		}
		JSONObject jRet = new JSONObject();
		jRet.put("user_id", member.getId());
		jRet.put("nick_name", member.getNickName());
		jRet.put("is_visitor", member.getIsVisitor());
		jRet.put("is_vip", 2);
		jRet.put("promo_code", member.getPromoCode());
		jRet.put("promo_qrcode", CommUtil.getHost(member.getPromoQrcode()));
		jRet.put("promo_url", member.getPromoCode());
		jRet.put("day_view_times", member.getDayViewTimes());
		jRet.put("today_view_times", member.getTodayViewTimes());
		jRet.put("re_today_view_times", member.getReTodayViewTimes());
		jRet.put("accessToken", TokenUtil.genToken(member.getAccount(), member.getId()));
		
		if (member.getExpireTime() > now()){
			jRet.put("is_vip", 1);
			jRet.put("expire_time", member.getExpireTime());
			
			jRet.put("today_view_times", "无限");
			jRet.put("re_today_view_times", "无限");
		}
		return success(jRet);
	}
	

}
