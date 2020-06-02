package org.crow.movie.user.web.api.demo;

import java.util.ArrayList;
import java.util.List;

import org.crow.movie.user.common.ApplicationProperties;
import org.crow.movie.user.common.db.entity.MemberPromo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

@RestController
@RequestMapping("/demo")
public class DemoApi extends BaseController{

	@Autowired
    private ApplicationProperties properties;

	@RequestMapping(value="uploads")
	public String getUpload(){
		
		JSONObject jo = new JSONObject();
		jo.put("upload-dir", properties.getUploadDir());
		jo.put("qrcode", properties.getQrcodeDir());
		return jo.toJSONString();
	}
	
	@RequestMapping(value="bean")
	public ReturnT<?> beanTest(){
		
		MemberPromo entity = new MemberPromo();
		entity.setCreateTime(now());
		entity.setMemberId(2);
		// 未完待续
		return success(entity);

	}
	
	@RequestMapping(value="beanlist")
	public ReturnT<?> beanlistTest(){
		
		MemberPromo entity = new MemberPromo();
		entity.setCreateTime(now());
		entity.setMemberId(2);
		
		MemberPromo entity2 = new MemberPromo();
		entity.setCreateTime(now());
		entity.setMemberId(3);
		
		List<MemberPromo> list = new ArrayList<MemberPromo>(){
			private static final long serialVersionUID = 1L;

			{
				this.add(entity2);
				this.add(entity2);
			}
		};
		
		// 未完待续
		return success(list);

	}
}
