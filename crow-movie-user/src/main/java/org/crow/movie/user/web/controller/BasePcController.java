package org.crow.movie.user.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.db.entity.AdminInfo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.AdminInfoService;
import org.crow.movie.user.common.plugin.redis.RedisService;
import org.crow.movie.user.common.util.Php2JavaUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.common.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public abstract class BasePcController {

	@Resource
	private RedisService redisService;
	
	@Autowired
	private AdminInfoService adminInfoService;

	@Value("${movie.user.salt}")
	protected String salt;

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 用于根据token获取admin用户
	 * @param request
	 * @return
	 */
	protected AdminInfo getAdminInfo(HttpServletRequest request){
		String token = request.getHeader("accessToken");
		Integer id = TokenUtil.getUserID(token);
		return adminInfoService.getById(id);
	}
	
	protected int getAdminId(HttpServletRequest request){
		
		String token = request.getHeader("accessToken");
		return TokenUtil.getUserID(token);
	}

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
	
	protected int now(){
		return Php2JavaUtil.transTimeJ2P(System.currentTimeMillis());
	}
}
