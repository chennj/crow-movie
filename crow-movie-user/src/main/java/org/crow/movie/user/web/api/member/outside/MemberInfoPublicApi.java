package org.crow.movie.user.web.api.member.outside;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.cache.FixedCache;
import org.crow.movie.user.common.db.entity.AppAdv;
import org.crow.movie.user.common.db.entity.AppConfig;
import org.crow.movie.user.common.db.entity.AppLevel;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.entity.MemberSms;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberFeedbackService;
import org.crow.movie.user.common.db.service.MemberHistoryService;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.db.service.MemberMessageService;
import org.crow.movie.user.common.db.service.MemberPromoService;
import org.crow.movie.user.common.db.service.MemberSmsService;
import org.crow.movie.user.common.plugin.qrcode.QRCodeService;
import org.crow.movie.user.common.plugin.sms.Sms;
import org.crow.movie.user.common.util.MapUtil;
import org.crow.movie.user.common.util.Php2JavaUtil;
import org.crow.movie.user.common.util.RegexUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.common.util.CommUtil;
import org.crow.movie.user.common.util.DigestUtils;
import org.crow.movie.user.web.annotation.Permission;
import org.crow.movie.user.web.controller.BasePublicController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/public/mbrinfo")
@Permission(managerLimit=false)
@Api(tags = "User Info Related Interface Of Mobile/Pc",description="手机/PC用户信息相关接口,需要token")
public class MemberInfoPublicApi extends BasePublicController{
	
	@Autowired
	private MemberInfoService memberInfoService;
	
	@Autowired
	private MemberHistoryService memberHistoryService;
	
	@Autowired
	private MemberPromoService memberPromoService;
	
	@Autowired
    private QRCodeService qrCodeService;
	
	@Autowired
	private MemberMessageService memberMessageService;
	
	@Autowired
	private MemberFeedbackService memberFeedbackService;
	
	@Autowired
	private MemberSmsService memberSmsService;
	

	/**
	 * 二维码访问次数更新
	 * @param request
	 * @param allParams
	 * @return
	 */
	@ApiOperation(value = "二维码访问次数更新",notes="二维码访问次数更新")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="save-qrcode", method=RequestMethod.POST)
	public ReturnT<?> saveQrcode(HttpServletRequest request){

		logger.info("public.mbrinfo.save-qrcode>>>enter");
		
		MemberInfo userInfo = this.getUser();
		
		if (null == userInfo){
			return fail("用户已经不存在");
		}
		
		if (userInfo.getIsSaveQrcode() == 2){
			
			AppConfig appConfig = FixedCache.appConfigCache();
			
			Integer save_promo_qrcode_view_times = appConfig.getSavePromoQrcodeViewTimes();
			if (save_promo_qrcode_view_times != null && save_promo_qrcode_view_times > 0){
				
				userInfo.setDayViewTimes(userInfo.getDayViewTimes()+save_promo_qrcode_view_times);
				userInfo.setTodayViewTimes(userInfo.getTodayViewTimes()+save_promo_qrcode_view_times);
				userInfo.setReTodayCacheTimes(userInfo.getReTodayCacheTimes()+save_promo_qrcode_view_times);
			}
			
			userInfo.setIsSaveQrcode(1);
			userInfo.setSaveQrcodeTime(Php2JavaUtil.transTimeJ2P(System.currentTimeMillis()));
			userInfo.setUpdateTime(Php2JavaUtil.transTimeJ2P(System.currentTimeMillis()));
			memberInfoService.modify(userInfo);
		}
		
		return success();
	}
	
	@ApiOperation(value = "宣传推荐详情",notes="宣传推荐详情")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="promo-detail", method=RequestMethod.POST)
	public ReturnT<?> promodDail(HttpServletRequest request){
		
		logger.info("public.mbrinfo.promo-detail>>>enter");
		
		MemberInfo mi = this.getUser();
		if (null == mi){
			return fail("用户不存在");
		}
		
		JSONObject userInfo;
		try {
			userInfo = new JSONObject(MapUtil.objectToMap1(mi));
		} catch (Exception e) {			
			e.printStackTrace();
			logger.error("public.mbrinfo.promo-detail>>>"+e.getMessage());
			return fail(e.getMessage());
		}
		
		
		//我的等级信息
		Map<Integer, AppLevel> levelMap = FixedCache.appLevelCache();
		AppLevel current_level = levelMap.get(mi.getLevelId());
		
		JSONObject user_current_level = new JSONObject();
		user_current_level.put("title", 	current_level.getTitle());
		user_current_level.put("code", 		current_level.getCode());
		user_current_level.put("grade", 	current_level.getGrade());
		user_current_level.put("icon", 		CommUtil.getHost(current_level.getIcon()));
		user_current_level.put("next_per", 	0);
		
		Integer next_level_id = mi.getLevelId()+1;
		next_level_id = next_level_id<7 ? next_level_id : 6;
		AppLevel next_level = levelMap.get(next_level_id);
		if (null != next_level){
			user_current_level.put("next_title", 	next_level.getTitle());
			user_current_level.put("next_code",		next_level.getCode());
			user_current_level.put("next_grade",	next_level.getGrade());
			user_current_level.put("next_icon",		CommUtil.getHost(next_level.getIcon()));
			if (current_level.getPromoLimit() < next_level.getPromoLimit()){
				int promo_per = memberPromoService.count("memberId", mi.getId());
				int next_per = next_level.getPromoLimit() - promo_per;
				user_current_level.put("next_per", next_per);
			}
		}
		
		userInfo.put("level", user_current_level);
		
		AppConfig config = FixedCache.appConfigCache();
		JSONObject promo = new JSONObject(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			{
				this.put("click_adv_view_times", 		config.getClickAdvViewTimes());
				this.put("username_reg_view_times", 	config.getUsernameRegViewTimes());
				this.put("bind_phone_view_times", 		config.getBindPhoneViewTimes());
				this.put("mobile_reg_view_times", 		config.getUsernameRegViewTimes());
				this.put("reg_promo_view_times", 		config.getRegPromoViewTimes());
				this.put("save_promo_qrcode_view_times",config.getSavePromoQrcodeViewTimes());
			}
			
		};
		
		List<AppLevel> app_level_list = FixedCache.appLevelListCache();
		JSONArray jary_app_level_list = new JSONArray();
		for (AppLevel one : app_level_list){
			JSONObject j1 = new JSONObject();
			j1.put("title", one.getTitle()+"徽章");
			j1.put("icon", CommUtil.getHost(one.getIcon()));
			j1.put("day_view_times", one.getDayViewTimes() != 999 ? one.getDayViewTimes() : "无限");
			JSONObject j2 = new JSONObject();
			j2.put(String.valueOf(one.getId()), j1);
			jary_app_level_list.add(j2);
		}
		
		promo.put("level", jary_app_level_list);
		userInfo.put("promo", promo);
		
		return success(userInfo);
	}
	
	@ApiOperation(value = "改变性别",notes="改变性别")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="update-sex", method=RequestMethod.POST)
	public ReturnT<?> updateSex(HttpServletRequest request,
			@RequestParam(required=true) Integer sex){
		
		logger.info("public.mbrinfo.update-sex>>>enter");
		
		if (sex != 1 && sex != 2){
			return fail("参数错误");
		}
		
		MemberInfo user = this.getUser();
		
		user.setSex(sex);
		user.setUpdateTime(now());
		user.setIsSex(1);
		
		user = memberInfoService.modify(user);
		if (null == user){
			return fail("更新失败 ");
		} else {
			return success();
		}
	}
	
	@ApiOperation(value = "改变呢称",notes="改变呢称")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="update-nickname", method=RequestMethod.POST)
	public ReturnT<?> updateNickname(HttpServletRequest request,
			@RequestParam(required=true) String nickName){
		
		logger.info("public.mbrinfo.update-sex>>>enter");
		
		if (StrUtil.isEmpty(nickName)){
			return fail("昵称不能为空");
		}
		if (nickName.length()<4 || nickName.length()>12){
			return fail("昵称长度有误");
		}
		
		MemberInfo user = this.getUser();
		
		user.setNickName(nickName);
		user.setUpdateTime(now());
		user.setIsNickName(1);
		
		user = memberInfoService.modify(user);
		if (null == user){
			return fail("更新失败 ");
		} else {
			return success();
		}
	}
	
	/**
	 * 更新头像
	 * @param request
	 * @param file
	 * @return
	 */
	@ApiOperation(value = "改变头像",notes="改变头像")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="update-avatar", method=RequestMethod.POST)
	public ReturnT<?> updateNickname(HttpServletRequest request,
			@RequestParam("file") MultipartFile file){
		
		logger.info("public.mbrinfo.update-avatar>>>enter");
		
		if (file.isEmpty()){
			return fail("头像不能为空");
		}
		
		//check file
		String[] fileExts = {"jpg","jpeg","png"};
		String filename = file.getOriginalFilename();
		if (!Arrays.asList(fileExts).contains(filename.substring(filename.lastIndexOf(".")))){
			return fail("文件格式不正确");
		}
		
		MemberInfo user = this.getUser();

		try {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(appProperties.getFeedbackDir() + File.separator + file.getOriginalFilename());
            Files.write(path, bytes);
            user.setAvatar(file.getOriginalFilename());
            user.setIsAvatar(1);
            user.setUpdateTime(now());
            memberInfoService.modify(user);
        } catch (IOException e) {
            return fail(e.getMessage());
        }
		
		JSONObject jRet = new JSONObject();
		jRet.put("avatar", CommUtil.getAvatar(user.getAvatar()));
		
		return success(jRet);
	}
	
	/**
	 * 用户信息
	 * @return
	 */
	@ApiOperation(value = "用户信息",notes="用户信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="manage", method=RequestMethod.POST)
	public ReturnT<?> manage(){
		
		return success(this.getJUser());
	}
	
	/**
	 * 二维码
	 * @return
	 */
	@ApiOperation(value = "生成二维码",notes="生成二维码")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="promo-info", method=RequestMethod.POST)
	public ReturnT<?> promoInfo(){
				
		MemberInfo user = this.getUser();
		String promo_filename = "";
		AppConfig app_config = FixedCache.appConfigCache();
		
		// 如果没有二维码，生成新的二维码
		if (StrUtil.isEmpty(user.getPromoQrcode())){
			
			int width 	= 166;
			int height 	= 166;
			
			String qrcodeDir = appProperties.getQrcodeDir();
			String uploadDir = appProperties.getUploadDir();
			String promo_url = app_config.getPromoUrl();
			if (StrUtil.isEmpty(promo_url)){
				promo_url = "http://www.k3289.com/?code=";
			}
			String data = promo_url+this.getUser().getPromoCode();
			String imageName = String.format("%d_qr.png", this.getUser().getId());
	        File qrFile = new File(qrcodeDir, imageName);
	        qrCodeService.encode(data, width, height, qrFile);
	        
	        logger.info("生成的二维码文件:{}", qrFile.getAbsolutePath());
	        
	        File logoFile = new File(uploadDir, "logo.png");
	        imageName = String.format("%s_qr_logo.png", this.getUser().getId());
	        File logoQrFile = new File(qrcodeDir, imageName);
	        qrCodeService.encodeWithLogo(qrFile, logoFile, logoQrFile);
	        logger.info("生成的带logo二维码文件:{}", logoQrFile.getAbsolutePath());
	        
	        promo_filename = logoQrFile.getName();
	        user.setPromoQrcode(promo_filename);
	        memberInfoService.modify(user);
		}
		
		JSONObject jRet = this.getJUser();
		jRet.put("promo_top", 		app_config.getPromoTop());
		jRet.put("promo_tips", 		app_config.getPromoTips());
		jRet.put("promo_qrcode", 	promo_filename);
		jRet.put("promo_links", 	app_config.getPromoLinks()
				.replace("\\[code\\]", this.getUser().getPromoCode()));
		
		return success(jRet);
	}
	
	/**
	 * 用户主页
	 * @return
	 */
	@ApiOperation(value = "注册用户主页信息",notes="注册用户主页信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="index", method=RequestMethod.POST)
	public ReturnT<?> index(){
		
		JSONObject juser 	= this.getJUser();
		MemberInfo user 	= this.getUser();
		
		Map<String, Object> eq = new HashMap<>();
		eq.put("memberId", user.getId());
		eq.put("isRead", 1);
		
		/**
		 * 用户界面广告列表
		 */
		List<AppAdv> member_adv_list = this.getAdvMap().get("member");
		juser.put("adv", member_adv_list.get(new Random().nextInt(member_adv_list.size())));
		
		/**
		 * 检查是否有新的通知
		 */
		juser.put("is_new_message", 2);
		int user_message_count = memberMessageService.count(eq);
		if (user_message_count > 0){
			juser.put("is_new_message", 1);
		}
		
		/**
		 * 检查是否有新的反馈
		 */
		eq.put("adminIsRead", 2);
		juser.put("is_new_feedback", 2);
		int user_feedback_count = memberFeedbackService.count(eq);
		if (user_feedback_count > 0){
			juser.put("user_feedback_count", 1);
		}
		Map<Integer, AppLevel> app_level = FixedCache.appLevelCache();
		AppLevel current_level = app_level.get(user.getLevelId());
		Integer next_level_id = user.getLevelId() + 1;
		next_level_id = next_level_id<7 ? next_level_id : 6;
		AppLevel next_level = app_level.get(next_level_id);
		String next_icon = CommUtil.getHost(current_level.getIcon());
		int next_per = 0;
		if (null != next_level){
			next_icon = CommUtil.getHost(next_level.getIcon());
			if (current_level.getPromoLimit() < next_level.getPromoLimit()){
				int promo_per = memberPromoService.count("memberId",user.getId());
				next_per = next_level.getPromoLimit() - promo_per;
			} 
		}
		
		/**
		 * 我的等级信息
		 */
		AppConfig app_config = FixedCache.appConfigCache();
		JSONObject user_current_level = new JSONObject(){

			private static final long serialVersionUID = 1L;
			{
				this.put("title", 	current_level.getTitle());
				this.put("code", 	current_level.getCode());
				this.put("grade", 	current_level.getGrade());
				this.put("icon", 	CommUtil.getHost(current_level.getIcon()));
			}
		};
		user_current_level.put("next_icon", next_icon);
		user_current_level.put("next_per", 	next_per);
		
		juser.put("level", user_current_level);
		juser.put("chart_url", app_config.getChartUrl());
		
		/**
		 * 我的历史记录
		 */
		String 
		sql = "select count(1) from hg_member_history history "
			+ "join hg_app_movie movie on history.movie_id=movie.id "
			+ "where member_id = ?1 ";
		int user_history_count = memberHistoryService.countNative(sql, user.getId());
		if (user_history_count > 0 && user.getIsVisitor() == 2){
			JSONObject jo = new JSONObject();
			jo.put("total_num", user_history_count);
			sql = "select movie.id,movie.title,movie.cover "
				+ "from hg_member_history history "
				+ "join hg_app_movie movie on history.movie_id=movie.id "
				+ "where member_id = ?1 "
				+ "order by history.create_time desc";
			List<Map<String, Object>> user_history = 
					memberHistoryService.getPageListMap(sql, 1, 20, user.getId());
			for (Map<String, Object> one : user_history){
				one.put("cover", CommUtil.getHost(one.get("cover").toString()));
			}
			jo.put("movie_list", user_history);
			juser.put("history", jo);
		}
		
		/**
		 * 我的缓存
		 */
		sql = "select count(1) from hg_member_cache cache "
			+ "join hg_app_movie movie on cache.movie_id=movie.id "
			+ "where member_id = ?1 and device_id = ?2";
		user_history_count = memberHistoryService.countNative(sql, user.getId(),user.getDeviceId());
		if (user_history_count > 0){
			JSONObject jo = new JSONObject();
			jo.put("total_num", user_history_count);
			sql = "select movie.id,movie.title,movie.cover "
				+ "from hg_member_cache cache "
				+ "join hg_app_movie movie on cache.movie_id=movie.id "
				+ "where member_id = ?1 and device_id = ?2 "
				+ "order by cache.create_time desc";
			List<Map<String, Object>> user_history = 
					memberHistoryService.getPageListMap(sql, 1, 20, user.getId(), user.getDeviceId());
			for (Map<String, Object> one : user_history){
				one.put("cover", CommUtil.getHost(one.get("cover").toString()));
			}
			jo.put("movie_list", user_history);
			juser.put("cache", jo);
		}
		
		/**
		 * 我的喜欢
		 */
		sql = "select count(1) from hg_member_like mlike "
			+ "join hg_app_movie movie movie on mlike.movie_id=movie.id "
			+ "where member_id = ?1 ";
		user_history_count = memberHistoryService.countNative(sql, user.getId());
		if (user_history_count > 0){
			JSONObject jo = new JSONObject();
			jo.put("total_num", user_history_count);
			sql = "select movie.id,movie.title,movie.cover "
				+ "from hg_member_like mlike "
				+ "join hg_app_movie movie on mlike.movie_id=movie.id "
				+ "where member_id = ?1 "
				+ "order by mlike.create_time desc";
			List<Map<String, Object>> user_history = 
					memberHistoryService.getPageListMap(sql, 1, 20, user.getId());
			for (Map<String, Object> one : user_history){
				one.put("cover", CommUtil.getHost(one.get("cover").toString()));
			}
			jo.put("movie_list", user_history);
			juser.put("like", jo);
		}
		
		return success(juser);
	}
	
	@ApiOperation(value = "注册用户个人基本信息",notes="注册用户个人基本信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="info", method=RequestMethod.POST)
	public ReturnT<?> info(){
		
		return success(this.getJUser());
	}
	
	@ApiOperation(value = "用户更改密码",notes="用户更改密码")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="change-password", method=RequestMethod.POST)
	public ReturnT<?> changPassword(
			@RequestParam(required = true) String password,
			@RequestParam(required = true) String verify_code){
		
		MemberInfo user = this.getUser();
		if (StrUtil.isEmpty(user.getMobile()) || StrUtil.isEmpty(user.getGlobalAreaCode())){
			return fail("请先绑定手机号码");
		}
		// param
		if (StrUtil.isEmpty(password)){
			return fail("密码不能为空");
		}
		if (password.length() < 6 || password.length() > 20){
			return fail("密码长度有误");
		}
		if (RegexUtil.isNotNumOrChar(password)){
			return fail("密码只能是数字、字母");
		}
		
		if (!this.checkSmsCode(user.getMobile(), user.getDeviceId(), verify_code)){
			return fail("验证码错误");
		}
		
		user.setPassword(DigestUtils.encryptPwd(password));
		user.setUpdateTime(now());
		memberInfoService.modify(user);
		
		return success();
	}
	
	@ApiOperation(value = "手机号注册用户更改密码短信",notes="手机号注册用户更改密码短信")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="send-sms-code-by-change-password", method=RequestMethod.POST)
	public ReturnT<?> smsChangePassword(){
		
		MemberInfo user = this.getUser();
		if (StrUtil.isEmpty(user.getMobile()) || StrUtil.isEmpty(user.getGlobalAreaCode())){
			return fail("请先绑定手机号码");
		}
		
		int now = now();
		Map<String, Object> eq = new HashMap<>();
		eq.put("memberId", user.getId());
		eq.put("mobile", user.getMobile());
		
		MemberSms mmsms = memberSmsService.getSingle("", eq);
		if (null != mmsms && now - mmsms.getSendTime() <= 60){
			return fail("60秒内只能发送一次验证码");
		}
		String code = Sms.getRandomCode(4);
		String content = "你本次的验证码为" + code;
		String res = Sms.send(user.getMobile(), content);
		JSONObject jres = JSON.parseObject(res);
		if ("0".equals(jres.getString("code"))){
			if (null != mmsms){
				mmsms.setSendTime(now);
				memberSmsService.modify(mmsms);
			} else {
				mmsms = new MemberSms();
				mmsms.setMemberId(user.getId());
				mmsms.setMobile(user.getMobile());
				mmsms.setSendTime(now);
				memberSmsService.add(mmsms);
			}
			this.setSmsCode(user.getMobile(), user.getDeviceId(), code);
			
			return success();
		}
		
		return fail(jres.getString("msg"));
		
	}

	@ApiOperation(value = "绑定手机",notes="绑定手机")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="deviceid",value="设备ID",required=true,paramType="header")
	})
	@RequestMapping(value="bind-mobile", method=RequestMethod.POST)
	public ReturnT<?> bindMobile(
			HttpServletRequest request,
			@RequestParam(required = true) String mobile,
			@RequestParam(required = true) String global_area_code,
			@RequestParam(required = true) String verify_code){
		
		String deviceid = request.getHeader("deviceid");
		if (StrUtil.isEmpty(deviceid)){
			return fail("请求头没有设备ID");
		}
		
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
		
		MemberInfo member = this.getUser();
		int count = memberInfoService.count("account",mobile);
		if (count > 0){
			return fail("手机号已经存在,请直接登录");
		}

		if (StrUtil.isEmpty(member.getMobile())){
			AppConfig app_config = FixedCache.appConfigCache();
			int bind_phone_view_times = app_config.getBindPhoneViewTimes();
			member.setDayViewTimes(member.getDayViewTimes()+bind_phone_view_times);
			member.setTodayViewTimes(member.getTodayViewTimes()+bind_phone_view_times);
			member.setReTodayViewTimes(member.getReTodayViewTimes()+bind_phone_view_times);
			member.setGlobalAreaCode(global_area_code);
			member.setMobile(mobile);
			member.setUpdateTime(now());
			memberInfoService.modify(member);
		}
		
		this.delSmsCode(deviceid);
		
		return success();
	}

}
