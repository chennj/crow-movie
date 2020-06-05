package org.crow.movie.user.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.ApplicationProperties;
import org.crow.movie.user.common.cache.FixedCache;
import org.crow.movie.user.common.db.entity.AppAdv;
import org.crow.movie.user.common.db.entity.AppLevel;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.entity.MemberPromo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.db.service.MemberPromoService;
import org.crow.movie.user.common.plugin.redis.RedisService;
import org.crow.movie.user.common.util.Php2JavaUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseController {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private RedisService redisService;
	
	@Autowired
	protected ApplicationProperties appProperties;
	
	@Autowired
	private MemberInfoService memberInfoService;

	@Autowired
	private MemberPromoService memberPromoService;
	
	private final SimpleDateFormat sftYMD = new SimpleDateFormat("yyyy-MM-dd");
	
	protected ReturnT<String> success(String msg){
		return new ReturnT<>(msg);
	}
	
	protected  ReturnT<String> success(){
		return new ReturnT<>("operator success");
	}
	
	protected  ReturnT<?> success(Object t){
		return new ReturnT<>(t);
	}
	
	protected  <T> ReturnT<T> fail(String msg){
		return new ReturnT<>(ReturnT.FAIL_CODE,msg);
	}
	
	protected  <T> ReturnT<T> fail(){
		return new ReturnT<>(ReturnT.FAIL_CODE,"operator failed");
	}
	
	protected String getIp(HttpServletRequest request){
		
		try {
			String ip = request.getHeader("X-Real-IP");
			if (!StrUtil.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)){
				return ip;
			}
			ip = request.getHeader("X-Forwarded-For");
			if (!StrUtil.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)){
				//多次反向代理会有多个ip，第一个为真实。
				int index = ip.indexOf(',');
				if (index != -1){
					return ip.substring(0, index);
				} else {
					return ip;
				}
			} else {
				return request.getRemoteAddr();
			}
		} catch (Exception e){
			return "null";
		}
	}
	
	protected boolean checkSmsCode(String mobile, String deviceid, String verifyCode){
		
		String cache_mobile 	= deviceid+"_mobile";
		String cache_sms_code 	= deviceid+"_sms_code";
		if(mobile.equals(redisService.get(cache_mobile)) && verifyCode.equals(redisService.get(cache_sms_code))){
			return true;
		}else{
			return false;
		}
	}
	
	protected void delSmsCode(String deviceid){
		
		String cache_mobile 	= deviceid+"_mobile";
		String cache_sms_code 	= deviceid+"_sms_code";
		redisService.del(cache_mobile,cache_sms_code);
	}
	
	protected void setRandomCode(String deviceid,String verifyCode){
		
		String cache_random_code = deviceid+"_random_code";
		redisService.set(cache_random_code, cache_random_code, 600);
	}

	protected boolean checkRandomCode(String deviceid, String verifyCode){
		
		String cache_random_code = deviceid+"_random_code";
		if (verifyCode.equals(redisService.get(cache_random_code))){
			return true;
		} else {
			return false;
		}
	}
	
	protected void delRandomCode(String deviceid){
		
		String cache_random_code = deviceid+"_random_code";
		redisService.del(cache_random_code);
	}

	protected void setSmsCode(String mobile, String deviceid, String verifyCode) {

		String cache_mobile 	= deviceid+"_mobile";
		String cache_sms_code 	= deviceid+"_sms_code";
		redisService.set(cache_mobile, mobile, 60);
		redisService.set(cache_sms_code, verifyCode, 60);

	}
	
	protected boolean checkSmsCodeIsValid(String mobile, String deviceid){
		String cache_mobile 	= deviceid+"_mobile";
		String cache_sms_code 	= deviceid+"_sms_code";
		return 0<redisService.exists(cache_mobile,cache_sms_code);
	}
	
	protected int now(){
		return Php2JavaUtil.transTimeJ2P(System.currentTimeMillis());
	}
	
	protected String getDid(HttpServletRequest request){
		return request.getHeader("deviceid");
	}
	
	protected boolean checkDeviceid(HttpServletRequest request){
		
		String deviceid = getDid(request);
		
		if (StrUtil.isEmpty(deviceid)){
			return false;
		}		
		return true;
	}

	protected void promoAction(MemberInfo from_member_info, Integer to_member_id){
		
		MemberPromo promo = new MemberPromo();
		promo.setMemberId(from_member_info.getId());
		promo.setToMemberId(to_member_id);
		promo.setCreateTime(now());
		memberPromoService.add(promo);
		
		Map<Integer, AppLevel> app_level = FixedCache.appLevelCache();
		int promo_per = memberPromoService.count("memberId", from_member_info.getId());
		
		AppLevel from_member_level 		= app_level.get(from_member_info.getLevelId());
		AppLevel from_member_next_level = app_level.get(from_member_info.getLevelId() + 1);
		if (from_member_level.getPromoLimit() < from_member_next_level.getPromoLimit()){
			int day_view_times = from_member_next_level.getDayViewTimes();
			from_member_info.setUpdateTime(now());
			from_member_info.setDayCacheTimes(from_member_info.getDayCacheTimes()+1);
			from_member_info.setTodayCacheTimes(from_member_info.getTodayCacheTimes()+1);
			from_member_info.setReTodayCacheTimes(from_member_info.getReTodayCacheTimes()+1);
			if (promo_per == from_member_next_level.getPromoLimit()){
				from_member_info.setLevelId(from_member_next_level.getId());
				from_member_info.setDayViewTimes(from_member_info.getDayViewTimes()+day_view_times);
				from_member_info.setTodayViewTimes(from_member_info.getTodayViewTimes()+day_view_times);
				from_member_info.setReTodayViewTimes(from_member_info.getReTodayViewTimes()+day_view_times);
			}
			memberInfoService.modify(from_member_info);
		}
	}
	
	protected Map<String, List<AppAdv>> getAdvMap(){
		
		List<AppAdv> adv_list = FixedCache.appAdvCache();
		Map<String, List<AppAdv>> adv_map = new HashMap<>();
		for (AppAdv one : adv_list){
			List<AppAdv> tmpAdvList = adv_map.get(one.getBelongArea());
			if (null == tmpAdvList){
				tmpAdvList = new ArrayList<AppAdv>();
				tmpAdvList.add(one);
				adv_map.put(one.getBelongArea(), tmpAdvList);
			} else {
				tmpAdvList.add(one);
			}
		}
		
		return adv_map;
	}
	
	
	protected String ts(int i){
		long l = Php2JavaUtil.transTimeP2J(i);
		return this.sftYMD.format(l);
	}
	
	protected Integer o2i(Object object){
		return Integer.valueOf(String.valueOf(object));
	}

}
