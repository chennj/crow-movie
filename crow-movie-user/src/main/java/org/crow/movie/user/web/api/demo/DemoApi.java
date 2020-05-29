package org.crow.movie.user.web.api.demo;

import org.crow.movie.user.common.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/demo")
public class DemoApi{

	@Autowired
    private ApplicationProperties properties;

	@RequestMapping(value="uploads")
	@ResponseBody
	public String getUpload(){
		
		JSONObject jo = new JSONObject();
		jo.put("upload-dir", properties.getUploadDir());
		jo.put("qrcode", properties.getQrcodeDir());
		return jo.toJSONString();
	}
}
