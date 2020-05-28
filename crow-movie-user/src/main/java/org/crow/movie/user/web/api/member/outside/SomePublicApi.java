package org.crow.movie.user.web.api.member.outside;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.db.Page;
import org.crow.movie.user.common.db.entity.AppQuestions;
import org.crow.movie.user.common.db.entity.MemberMessage;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.AppQuestionsService;
import org.crow.movie.user.common.db.service.MemberMessageService;
import org.crow.movie.user.web.controller.BasePublicController;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * 
 * @author chenn
 *
 */
@RestController
@RequestMapping("/public/some")
public class SomePublicApi extends BasePublicController{
	
	@Autowired
	AppQuestionsService appQuestionsService;
	
	@Autowired
	MemberMessageService memberMessageService;

	@RequestMapping(value="question-show", method=RequestMethod.POST)
	public ReturnT<?> questionShow(HttpServletRequest request,
			@RequestParam(required=true) Integer id){
	
		try {
			AppQuestions result = new AppQuestions();
			AppQuestions message = appQuestionsService.getById(id);
			BeanUtils.copyProperties(message, result, new String[]{"id","sort","status","createUser","updateTime","updateUser"});
			return success(result);
		} catch (Exception e){
			return fail("未找到相关数据");
		}
	}
	
	@RequestMapping(value="question", method=RequestMethod.POST)
	public ReturnT<?> question(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams){
	
		Map<String, Object> eq = new HashMap<>();
		eq.put("status", 1);
		Page<AppQuestions> page = appQuestionsService.page(
				Integer.valueOf(allParams.getOrDefault("page", 1).toString()), 
				Integer.valueOf(allParams.getOrDefault("pageSize", 20).toString()),
				eq);
		JSONArray jRet = new JSONArray();
		for (AppQuestions one : page.getResults()){
			JSONObject jo = new JSONObject();
			jo.put("id",one.getId());
			jo.put("title", one.getTitle());
			jo.put("content", one.getContent());
			jo.put("create_time", this.ts(one.getCreateTime()));
			jRet.add(jo);
		}
		return success(jRet);
	}
	
	@RequestMapping(value="message-show", method=RequestMethod.POST)
	public ReturnT<?> messageShow(
			@RequestParam(required=true) Integer id,
			@RequestParam(defaultValue="1") Integer type){
	
		MemberMessage message = null;
		Map<String, Object> eq = new HashMap<>();
		eq.put("memberId", this.getUser().getId());
		eq.put("id", id);
		eq.put("messageType", type);
		try {
			message = memberMessageService.getUnique(eq);
		} catch (Exception e){
			return fail("未找到相关数据");
		}
		
		MemberMessage result = new MemberMessage();
		BeanUtils.copyProperties(message, result, 
				new String[]{"id","messageType","memberId","isRead","noticeId","createUser","updateTime","updateUser"});
		
		if (message.getIsRead() == 1){
			message.setIsRead(2);
			message.setUpdateTime(now());
			memberMessageService.modify(message);
		}
		return success(result);
	}
	
	@RequestMapping(value="notice-show", method=RequestMethod.POST)
	public ReturnT<?> noticeShow(HttpServletRequest request,
			@RequestParam(required=true) Integer id){
		
		return messageShow(id, 2);
	}
	
	@RequestMapping(value="message", method=RequestMethod.POST)
	public ReturnT<?> message(
			@RequestParam Map<String,Object> allParams){
	
		Map<String, Object> eq = new HashMap<>();
		eq.put("memberId", this.getUser().getId());
		eq.put("messageType", 1);
		Page<MemberMessage> page = memberMessageService.page(
				Integer.valueOf(allParams.getOrDefault("page", 1).toString()), 
				Integer.valueOf(allParams.getOrDefault("pageSize", 20).toString()),
				"isRead asc,createTime desc",
				eq);
		JSONArray jRet = new JSONArray();
		for (MemberMessage one : page.getResults()){
			JSONObject jo = new JSONObject();
			jo.put("id",one.getId());
			jo.put("title", one.getTitle());
			jo.put("content", one.getContent());
			jo.put("create_time", this.ts(one.getCreateTime()));
			jRet.add(jo);
		}
		return success(jRet);
	}
}
