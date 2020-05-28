package org.crow.movie.user.web.api.member;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberCacheService;
import org.crow.movie.user.common.util.CommUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

@RestController
@RequestMapping("/mbrcache")
public class MemberCacheApi extends BaseController{

	@Autowired
	MemberCacheService memberCacheService;
	
	/**
	 * 搜索统计
	 * @param request
	 * @param allParams
	 * @return
	 */
	@RequestMapping(value="search-count", method=RequestMethod.POST)
	public ReturnT<?> searchCount(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams){

		logger.info("mbrcache.search>>>enter,recive data="+allParams.entrySet());
		
		Map<String, List<Map<String, Object>>> allMap 	= memberCacheService.search(
				Integer.valueOf(allParams.getOrDefault("page", 1).toString()), 
				Integer.valueOf(allParams.getOrDefault("pageSize", 20).toString()), 
				allParams);
		
		List<Map<String, Object>> list = allMap.get("list");
		
		JSONObject jRet = new JSONObject(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				this.put("list", list);
				this.put("condition", allParams);
			}
		};
		
		return success(jRet);
	}
	
	/**
	 * 获取用户缓存表中的视频文件大小与路径
	 * @param request
	 * @param allParams
	 * @return
	 */
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

	@RequestMapping(value="dels", method=RequestMethod.POST)
	public ReturnT<?> dels(
			@RequestParam(required = true) String ids,
			@RequestParam(required = true) Integer memberId,
			@RequestParam(required = true) String deviceId){
		
		if (StrUtil.isEmpty(ids) || StrUtil.isEmpty(memberId) || StrUtil.isEmpty(deviceId)){
			return fail("ids or memberId or deviceId is empty");
		}
		
		logger.info("mbrcache.del>>>enter,recive data="+ids+","+memberId+","+deviceId);
		
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
	
}
