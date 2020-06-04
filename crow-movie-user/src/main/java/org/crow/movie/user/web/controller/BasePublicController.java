package org.crow.movie.user.web.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.plugin.qrcode.QRCodeService;
import org.crow.movie.user.common.util.MapUtil;
import org.crow.movie.user.common.util.Php2JavaUtil;
import org.crow.movie.user.common.util.CommUtil;
import org.crow.movie.user.common.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

public abstract class BasePublicController extends BaseController{

	@Autowired
	private MemberInfoService memberInfoService;
	
	@Autowired
	protected QRCodeService qrCodeService;
	
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
	
	protected int getMemberId(HttpServletRequest request){
		
		return user.getId();
	}
	
	protected void saveUploadedFiles(List<MultipartFile> files) throws IOException {

        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue; 
            }

            byte[] bytes = file.getBytes();
            Path path = Paths.get(appProperties.getUploadDir() + File.separator + file.getOriginalFilename());
            Files.write(path, bytes);
        }

    }
	
	protected File genQrcodePng(){
		
		int width 	= 166;
		int height 	= 166;
		
		String qrcodeDir = appProperties.getQrcodeDir();
		String uploadDir = appProperties.getUploadDir();
		String promo_url = CommUtil.getDomain()+"?code=";
        
		String data = promo_url+user.getPromoCode();
		String imageName = String.format("%d_qr.png", user.getId());
        File qrFile = new File(qrcodeDir, imageName);
        qrCodeService.encode(data, width, height, qrFile);
        
        logger.info("生成的二维码文件:{}", qrFile.getAbsolutePath());
        
        File logoFile = new File(uploadDir, "logo.png");
        imageName = String.format("%s_qr_logo.png", user.getId());
        File logoQrFile = new File(qrcodeDir, imageName);
        qrCodeService.encodeWithLogo(qrFile, logoFile, logoQrFile);
        logger.info("生成的带logo二维码文件:{}", logoQrFile.getAbsolutePath());
        
        String promo_filename = logoQrFile.getName();
        user.setPromoQrcode(promo_filename);
        memberInfoService.modify(user);
        return logoQrFile;
	}
}
