package org.crow.movie.user.web.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.constant.Const;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.util.MapUtil;
import org.crow.movie.user.common.util.Php2JavaUtil;
import org.crow.movie.user.common.util.CommUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.common.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

public abstract class BasePublicController {

	@Autowired
	private MemberInfoService memberInfoService;

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private JSONObject juser = null;
	
	private MemberInfo user = null;
	
	private final SimpleDateFormat sftYMD = new SimpleDateFormat("yyyy-MM-dd");
	
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
	
	protected int getMemberId(HttpServletRequest request){
		
		return user.getId();
	}
	
	protected Integer o2i(Object object){
		return Integer.valueOf(String.valueOf(object));
	}

	protected int now(){
		return Php2JavaUtil.transTimeJ2P(System.currentTimeMillis());
	}
	
	protected String ts(int i){
		long l = Php2JavaUtil.transTimeP2J(i);
		return this.sftYMD.format(l);
	}
	protected ReturnT<String> success(String msg){
		return new ReturnT<>(msg);
	}
	
	protected  ReturnT<String> success(){
		return new ReturnT<>("操作成功");
	}
	
	protected  ReturnT<?> success(Object t){
		return new ReturnT<>(t);
	}
	
	protected  <T> ReturnT<T> fail(String msg){
		return new ReturnT<>(ReturnT.FAIL_CODE,msg);
	}
	
	protected  <T> ReturnT<T> fail(){
		return new ReturnT<>(ReturnT.FAIL_CODE,"操作失败");
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
	
	protected void saveUploadedFiles(List<MultipartFile> files) throws IOException {

        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue; 
            }

            byte[] bytes = file.getBytes();
            Path path = Paths.get(Const.FILE_UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);
        }

    }
}
