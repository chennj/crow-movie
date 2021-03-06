package org.crow.movie.user.web.controller;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.util.CommUtil;
import org.crow.movie.user.common.util.MapUtil;
import org.crow.movie.user.common.util.Php2JavaUtil;
import org.crow.movie.user.common.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.alibaba.fastjson.JSONObject;

public abstract class BasePcController extends BaseController{

	@Value("${movie.user.salt}")
	protected String salt;

	@Autowired
	private MemberInfoService memberInfoService;
	
	private JSONObject juser = null;
	
	private MemberInfo user = null;
	
	@ModelAttribute
	private void init(HttpServletRequest request){
		
		String token = request.getHeader("accessToken");
		Integer id = TokenUtil.getUserID(token);
		user = memberInfoService.getById(id);
		if (null != user){
			try {
				juser = new JSONObject(MapUtil.objectToMap2(user));
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("用户对象转json出错"+e.getMessage());
				return;
			}
			
			juser.put("sex", user.getSex()==null?0:user.getSex());
			juser.put("avatar", CommUtil.getAvatar(user.getAvatar()));
			juser.put("promo_qrcode", CommUtil.getHost(user.getPromoQrcode()));
			juser.put("is_vip", 2);
			juser.put("vip_hint", "SVIP");
			juser.put("expire_time1", user.getExpireTime());
			if (user.getExpireTime() > Php2JavaUtil.transTimeJ2P(System.currentTimeMillis())){
				juser.put("is_vip", 1);
				juser.put("expire_time", "于"+new SimpleDateFormat("yyyy-MM-dd HH:mm").format(user.getExpireTime()*1000)+"到期");
				juser.put("today_view_times", "无限");
				juser.put("re_today_view_times", "无限");
				juser.put("today_cache_times", "无限");
				juser.put("re_today_cache_times", "无限");
			} else {
				juser.put("expire_time","");
			}

		}
	}
	/**
	 * 根据token获取member
	 * @param request
	 * @return
	 */
	protected MemberInfo getUser(){
		return this.user;
	}
	
	protected JSONObject getJUser(){
		return this.juser;
	}

}
