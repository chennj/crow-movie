package org.crow.movie.user.web.api.permission;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crow.movie.user.common.cache.FixedCache;
import org.crow.movie.user.common.db.entity.AppConfig;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.entity.MemberOpen;
import org.crow.movie.user.common.db.entity.WebReg;
import org.crow.movie.user.common.db.model.BaseTypeWrapper;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.db.service.MemberOpenService;
import org.crow.movie.user.common.db.service.WebRegService;
import org.crow.movie.user.common.plugin.qrcode.QRCodeService;
import org.crow.movie.user.common.plugin.verifycode.VerifyCode;
import org.crow.movie.user.common.util.CommUtil;
import org.crow.movie.user.common.util.DigestUtils;
import org.crow.movie.user.common.util.IPUtil;
import org.crow.movie.user.common.util.RegexUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.common.util.TokenUtil;
import org.crow.movie.user.web.annotation.Permission;
import org.crow.movie.user.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/noAuth")
@Permission(memberLimit=false,managerLimit=false)
@Api(tags = "客户注册访问相关接口")
public class MemberPermission extends BaseController{

	@Autowired
	private MemberInfoService memberInfoService;
	
	@Autowired
	private WebRegService webRegService;

	@Autowired
    private QRCodeService qrCodeService;	

	@Autowired
	private MemberOpenService memberOpenService;

	@RequestMapping("isAlive")
    public String isAlive() {
		return "isAlive";
    }
	
	/**
	 * 手机注册
	 * @param request
	 * @param response
	 * @param allParams
	 * @return
	 */
	@ApiOperation("客户手机注册接口")
	@RequestMapping(value="register-mobile", method=RequestMethod.POST)
	public ReturnT<?> registerMobile(HttpServletRequest request, 
			HttpServletResponse response,
			@RequestParam Map<String,Object> allParams){
		
		logger.info("register mobile>>>接收到的数据："+allParams.entrySet());	
		
		String global_area_code = allParams.get("global_area_code").toString();
		if (StrUtil.isEmpty(global_area_code)){
			return fail("区号不能为空");
		}
		if (global_area_code.length()<1 || global_area_code.length()>32){
			return fail("区号长度有误");
		}
		if (RegexUtil.isNotNum(global_area_code)){
			return fail("区号只能是数字");
		}
		
		String mobile = allParams.get("mobile").toString();
		if (StrUtil.isEmpty(mobile)){
			return fail("手机号不能为空");
		}
		if (mobile.length()<6 || mobile.length()>32){
			return fail("手机号长度有误");
		}
		if (RegexUtil.isNotNum(mobile)){
			return fail("手机号只能是数字");
		}
		
		String password = allParams.get("password").toString();
		if (StrUtil.isEmpty(password)){
			return fail("密码不能为空");
		}
		if (password.length()<6 || password.length()>15){
			return fail("密码长度有误");
		}
		if (RegexUtil.isNotNumOrChar(password)){
			return fail("密码只能是数字");
		}
		
		String deviceid = request.getHeader("deviceid");
		if (StrUtil.isEmpty(deviceid)){
			return fail("设备ID不能为空");
		}
		
		String promo_code = allParams.getOrDefault("promo_code","").toString();
		if (promo_code.length() <= 0 || promo_code.length() > 20){
			return fail("推广码长度有误");
		}
		
		String verify_code = allParams.get("verify_code").toString();
		if (StrUtil.isEmpty(verify_code)){
			return fail("验证码不能为空");
		}
		if (verify_code.length()<1 || verify_code.length()>10){
			return fail("验证码长度有误");
		}
		if (RegexUtil.isNotNum(verify_code)){
			return fail("验证码只能是数字");
		}
		if (!this.checkSmsCode(mobile, deviceid, verify_code)){
			return fail("验证码错误");
		}
		
		int count = memberInfoService.count("account",mobile);
		if (count > 0){
			return fail("账号已经存在,请直接登录");
		}
		
		AppConfig app_config = FixedCache.appConfigCache();
		
		MemberInfo member = new MemberInfo();
		MemberInfo from_member_info = null;
		
		int day_view_times 			= app_config.getUsernameRegViewTimes();
		int bind_phone_view_times	= app_config.getBindPhoneViewTimes();
		int reg_promo_view_times 	= app_config.getRegPromoViewTimes();
		day_view_times += bind_phone_view_times;
		if (StrUtil.notEmpty(promo_code)){
			from_member_info = memberInfoService.getSingle("promoCode", promo_code);
			if (null == from_member_info){
				return fail("推广码有误");
			}
			day_view_times += reg_promo_view_times;
			member.setRegPromoCode(promo_code);
		}
		
		member.setAccount(mobile);
		member.setGlobalAreaCode(global_area_code);
		member.setMobile(mobile);
		member.setNickName(mobile);
		member.setDeviceId(deviceid);
		member.setPassword(DigestUtils.encryptPwd(DigestUtils.encryptMd5(password)));
		member.setLevelId(1);
		member.setDayViewTimes(day_view_times);
		member.setTodayViewTimes(day_view_times);
		member.setReTodayViewTimes(day_view_times);
		member.setDayCacheTimes(0);
		member.setTodayCacheTimes(0);
		member.setReTodayCacheTimes(0);
		member.setIsVisitor(2);
		member.setToken(DigestUtils.random(32));
		member.setCreateIp(IPUtil.getClientIp(request));
		member.setCreateTime(now());
		member.setPromoCode(DigestUtils.random(12,"upper_number"));
		boolean isReg = false;
		for(int i=0; i<3; i++){
			//最多执行3次
			try {
				member = memberInfoService.add(member);
				isReg = true;
				break;
			} catch (Exception e){
				member.setPromoCode(DigestUtils.random(12,"upper_number"));
				continue;
			} 
		}
		
		if (!isReg){
			return fail("注册失败:系统繁忙，稍后再试");
		}
		
		this.delSmsCode(deviceid);
		
		if (null != from_member_info){
			promoAction(from_member_info, member.getId());
		}
		
		String 
		sql = "insert into hg_member_message (message_type,member_id,title,content,is_read,create_time)  select '2',"
			+ member.getId()+","
			+ "title,content,'1',create_time from hg_app_notice where status=1";
		memberInfoService.execNativeSql(sql, new Object[]{});
		
		JSONObject jRet = new JSONObject();
		jRet.put("user_id", member.getId());
		jRet.put("token", member.getToken());
		return success(jRet);
	}
	
	/**
	 * 账户注册
	 * @param request
	 * @param response
	 * @param allParams
	 * @return
	 */
	@RequestMapping(value="register-account", method=RequestMethod.POST)
	public ReturnT<?> registerAccount(
			HttpServletRequest request, 
			HttpServletResponse response,
			@RequestParam Map<String,Object> allParams){
		
		logger.info("register account>>>接收到的数据："+allParams.entrySet());		

		String account 		= allParams.get("account").toString();
		String password 	= allParams.get("password").toString();
		String deviceid 	= allParams.get("deviceid").toString();
		String promo_code 	= allParams.getOrDefault("promo_code", "").toString();
		String verify_code	= allParams.get("verify_code").toString();
				
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
		
		if (promo_code.length()<0 || promo_code.length()>20){
			return fail("推广码长度有误");
		}
		
		if (StrUtil.isEmpty(verify_code)){
			return fail("验证码不能为空");
		}
		if (verify_code.length()<1 || verify_code.length()>10){
			return fail("验证码长度有误");
		}
		if (RegexUtil.isNotNum(verify_code)){
			return fail("验证码只能是数字");
		}
			
		int count = memberInfoService.count("account",account);
		if (count > 0){
			return fail("账号已经存在,请直接登录");
		}
		
		MemberInfo member = new MemberInfo();
		MemberInfo from_member_info = null;
		
		if (StrUtil.notEmpty(deviceid)){
			if (!this.checkRandomCode(deviceid, verify_code)){
				return fail("验证码错误");
			}
			Map<String, Object> eq = new HashMap<>();
			eq.put("deviceId", deviceid);
			eq.put("isVisitor", 2);			
			int account_user_count = memberInfoService.count(eq);
			if (account_user_count > 0){
				return fail("每个设备仅允许用户名注册一次");
			}
			member.setDeviceId(deviceid);
		} else {
			return fail("需要设备ID");
		}
		
		AppConfig app_config = FixedCache.appConfigCache();
		
		int day_view_times 			= app_config.getUsernameRegViewTimes();
		int reg_promo_view_times 	= app_config.getRegPromoViewTimes();
		if (StrUtil.notEmpty(promo_code)){
			from_member_info = memberInfoService.getSingle("promoCode", promo_code);
			if (null == from_member_info){
				return fail("推广码有误");
			}
			day_view_times += reg_promo_view_times;
			member.setRegPromoCode(promo_code);
		}
		
		member.setAccount(account);
		member.setNickName(account);
		member.setPassword(DigestUtils.encryptPwd(DigestUtils.encryptMd5(password)));
		member.setLevelId(1);
		member.setDayViewTimes(day_view_times);
		member.setTodayViewTimes(day_view_times);
		member.setReTodayViewTimes(day_view_times);
		member.setDayCacheTimes(0);
		member.setTodayCacheTimes(0);
		member.setReTodayCacheTimes(0);
		member.setIsVisitor(2);
		member.setToken(DigestUtils.random(32));
		member.setCreateIp(IPUtil.getClientIp(request));
		member.setCreateTime(now());
		member.setPromoCode(DigestUtils.random(12,"upper_number"));
		boolean isReg = false;
		for(int i=0; i<3; i++){
			//最多执行三次次
			try {
				member = memberInfoService.add(member);
				isReg = true;
				break;
			} catch (Exception e){
				member.setPromoCode(DigestUtils.random(12,"upper_number"));
				continue;
			} 
		}
		
		if (!isReg){
			return fail("注册失败:系统繁忙，稍后再试");
		}
		
		this.delRandomCode(deviceid);
		
		if (null != from_member_info){
			promoAction(from_member_info, member.getId());
		}
		
		String 
		sql = "insert into hg_member_message (message_type,member_id,title,content,is_read,create_time)  select '2',"
			+ member.getId()+","
			+ "title,content,'1',create_time from hg_app_notice where status=1";
		memberInfoService.execNativeSql(sql, new Object[]{});
		
		JSONObject jRet = new JSONObject();
		jRet.put("user_id", member.getId());
		jRet.put("accessToken", TokenUtil.genToken(member.getAccount(), member.getId()));
		return success(jRet);
	}
	
	/**
	 * 注册账户登录
	 * @param request
	 * @param response
	 * @param account
	 * @param password
	 * @return
	 */
	@RequestMapping(value="login-account", method=RequestMethod.POST)
	public ReturnT<?> loginAccount(
			HttpServletRequest request, 
			HttpServletResponse response, 
			@RequestParam(required=true)String account, 
			@RequestParam(required=true)String password){
		
		/*
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
		*/
		
		if (!this.checkDeviceid(request)){
			return fail("缺少设备ID");
		}
		
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

		String deviceid = this.getDid(request);
		if (!deviceid.equals(member.getDeviceId())){
			return fail("与预留设备不一致");
		}

		if (member.getStatus() == 1){
			return fail("用户已禁止登录");
		}
		if (member.getIsVisitor() == 1){
			return fail("游客禁止登录");
		}
		
		member.setToken(DigestUtils.random(32));
		member.setUpdateTime(now());
		if (StrUtil.isEmpty(member.getDeviceId())){
			member.setDeviceId(deviceid);
			String 
			sql = "insert into hg_member_message (message_type,member_id,title,content,is_read,create_time)  select '2',"
				+ member.getId()+","
				+ "title,content,'1',create_time from hg_app_notice where status=1";
			memberInfoService.execNativeSql(sql, new Object[]{});
		}
		memberInfoService.modify(member);
		
		JSONObject jRet = new JSONObject();
		jRet.put("user_id", member.getId());
		jRet.put("accessToken", TokenUtil.genToken(member.getAccount(), member.getId()));
		return success(jRet);
	}
	
	/**
	 * 游客登录
	 * @param request
	 * @param response
	 * @param account
	 * @param password
	 * @return
	 */
	@RequestMapping(value="login-visitor", method=RequestMethod.POST)
	public ReturnT<?> loginVisitor(
			HttpServletRequest request, 
			HttpServletResponse response, 
			@RequestParam(required=true)String account, 
			@RequestParam(required=true)String password){
		
		String deviceid = request.getHeader("deviceid");
		if (StrUtil.isEmpty(deviceid)){
			return fail("请求头没有设备ID");
		}
		
		Map<String, Object> eq = new HashMap<>();
		eq.put("deviceid", deviceid);
		eq.put("isVisitor", 1);
		MemberInfo member = memberInfoService.getUnique(eq);
		if (null == member){
			
			AppConfig app_config = FixedCache.appConfigCache();
			int visitor_reg_view_times = app_config.getVisitorRegViewTimes();
			
			member = new MemberInfo();
			member.setAccount(UUID.randomUUID().toString().replace("-", ""));
			member.setNickName("游客");
			member.setPassword(DigestUtils.encryptPwd(DigestUtils.encryptMd5(password)));
			member.setLevelId(1);
			member.setDayViewTimes(visitor_reg_view_times);
			member.setTodayViewTimes(visitor_reg_view_times);
			member.setReTodayViewTimes(visitor_reg_view_times);
			member.setDayCacheTimes(0);
			member.setTodayCacheTimes(0);
			member.setReTodayCacheTimes(0);
			member.setIsVisitor(1);
			member.setToken(DigestUtils.random(32));
			member.setCreateIp(IPUtil.getClientIp(request));
			member.setCreateTime(now());
			member.setPromoCode(DigestUtils.random(12,"upper_number"));
			boolean isReg = false;
			for(int i=0; i<3; i++){
				//最多执行三次次
				try {
					member = memberInfoService.add(member);
					isReg = true;
					break;
				} catch (Exception e){
					member.setPromoCode(DigestUtils.random(12,"upper_number"));
					continue;
				} 
			}
			
			if (!isReg){
				return fail("注册失败:系统繁忙，稍后再试");
			}
			String 
			sql = "insert into hg_member_message (message_type,member_id,title,content,is_read,create_time)  select '2',"
				+ member.getId()+","
				+ "title,content,'1',create_time from hg_app_notice where status=1";
			memberInfoService.execNativeSql(sql, new Object[]{});

		}
				
		JSONObject jRet = new JSONObject();
		jRet.put("user_id", member.getId());
		jRet.put("accessToken", TokenUtil.genToken(member.getAccount(), member.getId()));
		return success(jRet);
	}

	/**
	 * 手机号登录
	 * @param request
	 * @param response
	 * @param global_area_code
	 * @param mobile
	 * @param password
	 * @return
	 */
	@RequestMapping(value="login-mobile", method=RequestMethod.POST)
	public ReturnT<?> loginMobile(HttpServletRequest request, 
			HttpServletResponse response, 
			@RequestParam(required=true)String global_area_code,
			@RequestParam(required=true)String mobile, 
			@RequestParam(required=true)String password){
		
		if (StrUtil.isEmpty(global_area_code)){
			return fail("区号不能为空");
		}
		if (global_area_code.length()<1 || global_area_code.length()>32){
			return fail("区号长度有误");
		}
		if (RegexUtil.isNotNum(global_area_code)){
			return fail("区号只能是数字");
		}
		
		if (StrUtil.isEmpty(mobile)){
			return fail("手机号不能为空");
		}
		if (mobile.length()<6 || mobile.length()>32){
			return fail("手机号长度有误");
		}
		if (RegexUtil.isNotNum(mobile)){
			return fail("手机号只能是数字");
		}
		
		if (StrUtil.isEmpty(password)){
			return fail("密码不能为空");
		}
		if (password.length()<6 || password.length()>15){
			return fail("密码长度有误");
		}
		if (RegexUtil.isNotNumOrChar(password)){
			return fail("密码只能是数字");
		}
		
		Map<String,Object> eq = new HashMap<>();
		eq.put("global_area_code", global_area_code);
		eq.put("mobile", mobile);
		
		MemberInfo member = memberInfoService.getUnique(eq);
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

		String deviceid = this.getDid(request);
		if (!deviceid.equals(member.getDeviceId())){
			return fail("与预留设备不一致");
		}

		if (member.getStatus() == 1){
			return fail("用户已禁止登录");
		}
		if (member.getIsVisitor() == 1){
			return fail("游客禁止登录");
		}

		member.setUpdateTime(now());
		member.setToken(DigestUtils.random(32));
		memberInfoService.modify(member);
		
		JSONObject jRet = new JSONObject();
		jRet.put("user_id", member.getId());
		jRet.put("accessToken", TokenUtil.genToken(member.getAccount(), member.getId()));
		return success(jRet);
	}
	
	@RequestMapping(value="logout", method=RequestMethod.POST)
	public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response){
		
		//if (MemberPermissionInterceptor.ifMemberLogin(request)) {
		//	MemberPermissionInterceptor.logout(request, response);
		//}
		return success();
	}

	/**
	 * 生成验证码图片
	 * @param request
	 * @param response
	 */
    @RequestMapping("/getVerifyCode")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        if (StrUtil.isEmpty(request.getHeader("deviceid"))){
        	logger.error("noAuth.getVerifyCode>>>没有deviceid");
        	return;
        }
        try {
            
            response.setContentType("image/png");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Expire", "0");
            response.setHeader("Pragma", "no-cache");
            
            VerifyCode validateCode = new VerifyCode();
            
            // 直接返回图片
            BaseTypeWrapper<String> randomString = new BaseTypeWrapper<>();
            BufferedImage randomCodeImage = validateCode.getRandomCodeImage(request, response,randomString);
            this.delRandomCode(request.getHeader("deviceid"));
            ImageIO.write(randomCodeImage, "PNG", response.getOutputStream());
            
        } catch (Exception e) {
            System.out.println(e);
        }
        
    }
    
    /**
     * 忘记密码
     * @param request
     * @param mobile
     * @param password
     * @param verify_code
     * @return
     */
	@RequestMapping(value="forget-password", method=RequestMethod.POST)
	public ReturnT<?> forgetPassword(
			HttpServletRequest request,
			@RequestParam(required = true) String mobile,
			@RequestParam(required = true) String password,
			@RequestParam(required = true) String verify_code){
		
		if (StrUtil.isEmpty(mobile) ){
			return fail("手机号不能为空");
		}
		if (mobile.length()<6 || mobile.length()>32){
			return fail("手机号长度有误");
		}
		if (RegexUtil.isNotNum(mobile)){
			return fail("手机号只能是数字");
		}
		
		if (StrUtil.isEmpty(password) ){
			return fail("密码不能为空");
		}
		if (password.length()<6 || password.length()>20){
			return fail("密码长度有误");
		}
		if (RegexUtil.isNotNumOrChar(password)){
			return fail("密码只能是数字、字母");
		}
		
		if (StrUtil.isEmpty(verify_code) ){
			return fail("验证码不能为空");
		}
		if (verify_code.length()<1 || verify_code.length()>10){
			return fail("验证码长度有误");
		}
		if (RegexUtil.isNotNum(verify_code)){
			return fail("验证码只能是数字");
		}
		
		String deviceid = request.getHeader("deviceid");
		if (StrUtil.isEmpty(deviceid)){
			return fail("请求头缺少设备ID");
		}
		
		if (!this.checkSmsCode(mobile, deviceid, verify_code)){
			return fail("短信验证码错误");
		}
		
		MemberInfo member = memberInfoService.getUnique("mobile", mobile);
		if (null == member ){
			return fail("未找到当前用户,请先注册");
		}
		
		member.setUpdateTime(now());
		member.setPassword(DigestUtils.encryptPwd(password));
		memberInfoService.modify(member);
		this.delSmsCode(deviceid);
		
		JSONObject jRet = new JSONObject();
		jRet.put("user_id", member.getId());
		jRet.put("accessToken", TokenUtil.genToken(member.getAccount(), member.getId()));
		return success(jRet);
		
	}
	
	@RequestMapping(value="pc/login-account", method=RequestMethod.POST)
	public ReturnT<?> loginPcAccount(
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
	
	@RequestMapping(value="pc/reg", method=RequestMethod.POST)
	public ReturnT<?> reg(
			HttpServletRequest request, 
			HttpServletResponse response, 
			@RequestParam(required=true)String account, 
			@RequestParam(required=true)String password,
			@RequestParam String promo_code){
		
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
		
		if (StrUtil.notEmpty(promo_code) && promo_code.length() != 12){
			return fail("推广码长度有误");
		}
		
		MemberInfo member = memberInfoService.getUnique("account", account);
		if (null != member){
			return fail("账号已经存在");
		}
		
		member = new MemberInfo();
		
		AppConfig app_config = FixedCache.appConfigCache();
		int username_reg_view_times = app_config.getUsernameRegViewTimes();
		int reg_promo_view_times = app_config.getRegPromoViewTimes();
		int day_view_times = username_reg_view_times;
		MemberInfo from_member_info = null;
		if (StrUtil.notEmpty(promo_code)){
			from_member_info = memberInfoService.getSingle("promoCode", promo_code);
			if (null == from_member_info){
				return fail("推广码有误");
			}
			day_view_times += reg_promo_view_times;
			member.setRegPromoCode(promo_code);
		}
		
		WebReg webReg = new WebReg();
		webReg.setIp(IPUtil.getClientIp(request));
		try {
			webReg.setCreateDate(new SimpleDateFormat().parse("yyyy-MM-dd"));
		} catch (ParseException e1) {
			e1.printStackTrace();
			return fail(e1.getMessage());
		}
		try {
			webRegService.add(webReg);
		} catch (Exception e){
			return fail("你今日已注册");
		}
		
		member.setAccount(account);
		member.setNickName(account);
		member.setPassword(DigestUtils.encryptPwd(password));
		member.setLevelId(1);
		member.setDayViewTimes(day_view_times);
		member.setTodayViewTimes(day_view_times);
		member.setReTodayViewTimes(day_view_times);
		member.setDayCacheTimes(0);
		member.setTodayCacheTimes(0);
		member.setReTodayCacheTimes(0);
		member.setIsVisitor(2);
		member.setToken(DigestUtils.random(32));
		member.setCreateIp(IPUtil.getClientIp(request));
		member.setCreateAddr("");
		member.setCreateTime(now());
		member.setPromoCode(DigestUtils.random(12,"upper_number"));
		
		boolean isReg = false;
		for(int i=0; i<3; i++){
			//最多执行三次次
			try {
				member = memberInfoService.add(member);
				isReg = true;
				break;
			} catch (Exception e){
				member.setPromoCode(DigestUtils.random(12,"upper_number"));
				continue;
			} 
		}
		
		if (!isReg){
			return fail("注册失败:系统繁忙，稍后再试");
		}
		
		if (null != from_member_info){
			this.promoAction(from_member_info, member.getId());
		}

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
	
	@RequestMapping(value="pc/visitor-info", method=RequestMethod.POST)
	public ReturnT<?> visitorInfo(){
		
		AppConfig app_config = FixedCache.appConfigCache();
		JSONObject jRet = new JSONObject();
		
		jRet.put("is_visitor", 1);
		jRet.put("is_vip", 2);
		jRet.put("day_view_times", app_config.getPcVisitorRegViewTimes());
		jRet.put("today_view_times", app_config.getPcVisitorRegViewTimes());
		jRet.put("re_today_view_times", app_config.getPcVisitorRegViewTimes());
		
		return success(jRet);
	}
}
