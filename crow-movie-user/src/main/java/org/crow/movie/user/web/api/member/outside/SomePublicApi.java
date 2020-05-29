package org.crow.movie.user.web.api.member.outside;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.assertj.core.util.Arrays;
import org.crow.movie.user.common.db.Page;
import org.crow.movie.user.common.db.entity.AppQuestions;
import org.crow.movie.user.common.db.entity.MemberMessage;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.AppQuestionsService;
import org.crow.movie.user.common.db.service.MemberCacheService;
import org.crow.movie.user.common.db.service.MemberHistoryService;
import org.crow.movie.user.common.db.service.MemberLikeService;
import org.crow.movie.user.common.db.service.MemberMessageService;
import org.crow.movie.user.common.util.CommUtil;
import org.crow.movie.user.common.util.StrUtil;
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

	@Autowired
	MemberLikeService memberLikeService;
	
	@Autowired
	MemberCacheService memberCacheService;
	
	@Autowired
	MemberHistoryService memberHistoryService;
	
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
	
	@RequestMapping(value="notice", method=RequestMethod.POST)
	public ReturnT<?> notice(
			@RequestParam Map<String,Object> allParams){
	
		Map<String, Object> eq = new HashMap<>();
		eq.put("memberId", this.getUser().getId());
		eq.put("messageType", 2);
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
	
	@RequestMapping(value="like/dels", method=RequestMethod.POST)
	public ReturnT<?> likeDels(
			@RequestParam(required = true) String ids){
		
		if (StrUtil.isEmpty(ids)){
			return fail("ids or memberId or deviceId is empty");
		}
		
		Integer memberId = this.getUser().getId();
		logger.info("public.some.del>>>enter,recive data="+ids+","+memberId);
		
		Map<String, Object> eq = new HashMap<String, Object>(){
			
			private static final long serialVersionUID = 1L;

			{
				this.put("memberId", this);
			}
		};
		List<Object> list = new ArrayList<Object>(){

			private static final long serialVersionUID = 1L;

			{
				this.addAll(Arrays.asList(ids.split(",")));
			}
		};
		Map<String, List<Object>> in = new HashMap<>();
		in.put("id", list);
		
		int n = memberLikeService.delete(eq, null, in);
		if (n>0){
			return success();
		} else {
			return fail("删除失败");
		}
	}
	
	@RequestMapping(value="like", method=RequestMethod.POST)
	public ReturnT<?> like(
			@RequestParam(required = false,defaultValue = "1") Integer page,
			@RequestParam(required = false,defaultValue = "20") Integer pageSize){
		
		List<Map<String, Object>> list = memberLikeService.like(page,pageSize,this.getUser().getId());
		
		for (Map<String, Object> one : list){
			one.put("cover", CommUtil.getHost(String.valueOf(one.get("cover"))));
		}
		
		JSONObject jRet = new JSONObject(){
			
			private static final long serialVersionUID = 1L;

			{
				this.put("list", list);
			}
		};
		
		return success(jRet);
	}
	
	@RequestMapping(value="cache/dels", method=RequestMethod.POST)
	public ReturnT<?> cacheDels(
			@RequestParam(required = true) String ids){
		
		if (StrUtil.isEmpty(ids)){
			return fail("ids or memberId or deviceId is empty");
		}
		
		Integer memberId 	= this.getUser().getId();
		String deviceId 	= this.getUser().getDeviceId();
		String[] idss		= ids.split(",");
		
		List<Object> list = new ArrayList<Object>();
		for (String s : idss){
			list.add(Integer.valueOf(s));
		}
		logger.info("mbrcache.del>>>enter,recive data="+ids+","+memberId+","+deviceId);
		
		Map<String, Object> eq = new HashMap<String, Object>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				this.put("memberId", memberId);
				this.put("deviceId", deviceId);
			}
		};
		Map<String, List<Object>> in = new HashMap<>();
		in.put("id", list);
		
		int n = memberCacheService.delete(eq, null, in);
		if (n>0){
			return success();
		} else {
			return fail("删除失败");
		}
	}
	
	@RequestMapping(value="cache", method=RequestMethod.POST)
	public ReturnT<?> cache(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams){

		logger.info("mbrcache.cache>>>enter,recive data="+allParams.entrySet());
		
		Map<String, List<Map<String, Object>>> allMap 	= memberCacheService.cache(
				Integer.valueOf(allParams.getOrDefault("page", 1).toString()), 
				Integer.valueOf(allParams.getOrDefault("pageSize", 20).toString()), 
				allParams);
		
		List<Map<String, Object>> cacheList = allMap.get("cache_list");
		
		for (Map<String, Object> one : cacheList){
			one.put("cover", CommUtil.getHost(String.valueOf(one.get("cover"))));
			one.put("size", CommUtil.transByte(new File(System.getProperty("user.dir")+one.get("url2")).length()));
		}
		
		JSONObject jRet = new JSONObject(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				this.put("cache_list", cacheList);
				this.put("condition", allParams);
			}
		};
		
		return success(jRet);
	}
	
	@RequestMapping(value="history/dels", method=RequestMethod.POST)
	public ReturnT<?> historyDels(
			@RequestParam(required = true) String ids){
		
		if (StrUtil.isEmpty(ids)){
			return fail("ids or memberId or deviceId is empty");
		}
		
		Integer memberId 	= this.getUser().getId();
		String[] idss		= ids.split(",");
		
		List<Object> list = new ArrayList<Object>();
		for (String s : idss){
			list.add(Integer.valueOf(s));
		}
		logger.info("mbrcache.del>>>enter,recive data="+ids+","+memberId);
		
		Map<String, Object> eq = new HashMap<String, Object>(){

			private static final long serialVersionUID = 1L;

			{
				this.put("memberId", memberId);
			}
		};
		Map<String, List<Object>> in = new HashMap<>();
		in.put("id", list);
		
		int n = memberCacheService.delete(eq, null, in);
		if (n>0){
			return success();
		} else {
			return fail("删除失败");
		}
	}
	
	@RequestMapping(value="history", method=RequestMethod.POST)
	public ReturnT<?> history(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams){

		logger.info("mbrcache.cache>>>enter,recive data="+allParams.entrySet());
		
		final List<Map<String, Object>> list;
		try {
			list = memberHistoryService.history(
					Integer.valueOf(allParams.getOrDefault("page", 1).toString()), 
					Integer.valueOf(allParams.getOrDefault("pageSize", 20).toString()), 
					allParams,this.getUser().getId());
		} catch (NumberFormatException | ParseException e) {
			e.printStackTrace();
			return fail(e.getMessage());
		} 
		
		for (Map<String, Object> one : list){
			one.put("cover", CommUtil.getHost(String.valueOf(one.get("cover"))));
		}
		
		return success(list);
	}
}
