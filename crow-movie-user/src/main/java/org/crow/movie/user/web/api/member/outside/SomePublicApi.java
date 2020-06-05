package org.crow.movie.user.web.api.member.outside;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.crow.movie.user.web.annotation.Permission;
import org.crow.movie.user.web.controller.BasePublicController;
import org.crow.movie.user.web.interceptor.InterceptorFunc;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


/**
 * 
 * @author chenn
 *
 */
@RestController
@RequestMapping("/public/some")
@Permission(managerLimit=false)
@Api(tags = "Other Info Related Interface Of Mobile/Pc",description="其他公共接口，大部分都需要先获取token")
public class SomePublicApi extends BasePublicController{
	
	@Autowired
	private AppQuestionsService appQuestionsService;
	
	@Autowired
	private MemberMessageService memberMessageService;

	@Autowired
	private MemberLikeService memberLikeService;
	
	@Autowired
	private MemberCacheService memberCacheService;
	
	@Autowired
	private MemberHistoryService memberHistoryService;
	
	@ApiOperation(value = "用户问题获取",notes="根据id获取某个问题")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="id",value="问题id",required=true,paramType="query",dataType="integer")
	})
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
	
	@ApiOperation(value = "用户问题", notes="用户问题（分页）")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="allParams",value="文档缺陷，不需要填写",required=false,paramType="query"),
		@ApiImplicitParam(name="page",value="开始页",required=false,paramType="query"),
		@ApiImplicitParam(name="pageSize",value="页尺寸",required=false,paramType="query")
	})
	@RequestMapping(value="question", method=RequestMethod.POST)
	public ReturnT<?> question(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams){
	
		logger.info("public.some.question {}",allParams.entrySet());
		
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
	
	@ApiOperation(value = "用户消息", notes="根据用户id查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
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
	
	@ApiOperation(value = "用户通知", notes="根据用户id查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="notice-show", method=RequestMethod.POST)
	public ReturnT<?> noticeShow(HttpServletRequest request,
			@RequestParam(required=true) Integer id){
		
		return messageShow(id, 2);
	}
	
	@ApiOperation(value = "用户消息", notes="分页查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="allParams",value="文档缺陷，不需要填写",required=false,paramType="query"),
		@ApiImplicitParam(name="page",value="开始页",required=false,paramType="query"),
		@ApiImplicitParam(name="pageSize",value="页尺寸",required=false,paramType="query")
	})
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
	
	@ApiOperation(value = "用户通知", notes="分页查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="allParams",value="文档缺陷，不需要填写",required=false,paramType="query"),
		@ApiImplicitParam(name="page",value="开始页",required=false,paramType="query"),
		@ApiImplicitParam(name="pageSize",value="页尺寸",required=false,paramType="query")
	})
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
	
	@ApiOperation(value = "用户通知", notes="分页查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="ids",value="删除多个用‘，’分隔",required=true,paramType="query")
	})
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
	
	@ApiOperation(value = "用户爱好", notes="分页查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
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
	
	@ApiOperation(value = "用户缓存删除", notes="可删除多条")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="ids",value="删除多个用‘，’分隔",required=true,paramType="query")
	})
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
	
	@ApiOperation(value = "用户缓存查询", notes="分页查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="allParams",value="文档缺陷，不需要填写",required=false,paramType="query"),
		@ApiImplicitParam(name="page",value="开始页",required=false,paramType="query"),
		@ApiImplicitParam(name="pageSize",value="页尺寸",required=false,paramType="query")
	})
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
	
	@ApiOperation(value = "用户用户播放记录删除", notes="可删除多条")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="ids",value="删除多个用‘，’分隔",required=true,paramType="query")
	})
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
	
	@ApiOperation(value = "用户用户播放记录查询", notes="分页查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="allParams",value="文档缺陷，不需要填写",required=false,paramType="query"),
		@ApiImplicitParam(name="page",value="开始页",required=false,paramType="query"),
		@ApiImplicitParam(name="pageSize",value="页尺寸",required=false,paramType="query")
	})
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
	
	@ApiOperation(value = "二维码获取",notes="二维码", produces="image/png")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="qrcode", method={RequestMethod.POST, RequestMethod.GET})
	public void qrcode(
			HttpServletRequest request,
			HttpServletResponse response){
		
		String qrcodeDir = appProperties.getQrcodeDir();
		String imageName = this.getUser().getPromoQrcode();
		try {
	        File logoQrFile = new File(qrcodeDir, imageName);
	        if (!logoQrFile.exists() || StrUtil.isEmpty(imageName)){
	        	logoQrFile = this.genQrcodePng();
	        }
	        
            response.setContentType("image/png");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Expire", "0");
            response.setHeader("Pragma", "no-cache");

            // 直接返回图片
            BufferedImage qrcodeimg = ImageIO.read(logoQrFile);
            ImageIO.write(qrcodeimg, "PNG", response.getOutputStream());
		} catch (Exception e){
			e.printStackTrace();
			InterceptorFunc.FAIL(response, e.getMessage());
		}
	}
	
	@ApiOperation(value = "头像获取",notes="头像", produces="image/png")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="avatar", method={RequestMethod.POST, RequestMethod.GET})
	public void avatar(
			HttpServletRequest request,
			HttpServletResponse response){
		
		String avatarDir = appProperties.getAvatarDir();
		String imageName = this.getUser().getAvatar();
		try {
			
	        File avatarFile = new File(avatarDir, imageName);
	        if (!avatarFile.exists()){
	        	InterceptorFunc.FAIL(response, "头像不存在");
	        }
	        
            response.setContentType("image/png");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Expire", "0");
            response.setHeader("Pragma", "no-cache");

            // 直接返回图片
            BufferedImage avatarimg = ImageIO.read(avatarFile);
            ImageIO.write(avatarimg, "PNG", response.getOutputStream());
		} catch (Exception e){
			e.printStackTrace();
			InterceptorFunc.FAIL(response, e.getMessage());
		}
	}
}
