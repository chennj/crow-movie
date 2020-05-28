package org.crow.movie.user.web.api.member.outside;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.cache.FixedCache;
import org.crow.movie.user.common.db.entity.AppConfig;
import org.crow.movie.user.common.db.entity.AppLevel;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.db.service.MemberPromoService;
import org.crow.movie.user.common.util.MapUtil;
import org.crow.movie.user.common.util.Php2JavaUtil;
import org.crow.movie.user.common.util.CommUtil;
import org.crow.movie.user.web.annotation.Permission;
import org.crow.movie.user.web.controller.BasePublicController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/public/mbrinfo")
@Permission(managerLimit=false)
public class MemberInfoPublicApi extends BasePublicController{

	@Autowired
	MemberInfoService memberInfoService;
	
	@Autowired
	MemberPromoService memberPromoService;
	
	/**
	 * 保存二维码
	 * @param request
	 * @param allParams
	 * @return
	 */
	@RequestMapping(value="save-qrcode", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<?> saveQrcode(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams){

		logger.info("public.mbrinfo.save-qrcode>>>enter,recive data="+allParams.entrySet());
		
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
	
	@RequestMapping(value="detail", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<?> detail(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams){
		
		logger.info("public.mbrinfo.detail>>>enter,recive data="+allParams.entrySet());
		
		MemberInfo mi = this.getUser();
		if (null == mi){
			return fail("用户不存在");
		}
		
		JSONObject userInfo;
		try {
			userInfo = new JSONObject(MapUtil.objectToMap1(mi));
		} catch (Exception e) {			
			e.printStackTrace();
			logger.error("public.mbrinfo.detail>>>"+e.getMessage());
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
}
