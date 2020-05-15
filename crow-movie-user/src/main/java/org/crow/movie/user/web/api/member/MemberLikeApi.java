package org.crow.movie.user.web.api.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.assertj.core.util.Arrays;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberLikeService;
import org.crow.movie.user.common.util.SomeUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/mbrlike")
public class MemberLikeApi extends BaseController{

	@Autowired
	MemberLikeService memberLikeService;
	
	/**
	 * 搜索统计
	 * @param request
	 * @param allParams
	 * @return
	 */
	@RequestMapping(value="search-count", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<?> searchCount(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams){

		logger.info("mbrcache.search>>>enter,recive data="+allParams.entrySet());
		
		Map<String, List<Map<String, Object>>> allMap 	= memberLikeService.search(
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

	@RequestMapping(value="like", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<?> like(
			@RequestParam(required = true) String memberId,
			@RequestParam(required = false,defaultValue = "1") Integer page,
			@RequestParam(required = false,defaultValue = "20") Integer pageSize){
		
		List<Map<String, Object>> list = memberLikeService.like(page,pageSize,memberId);
		
		for (Map<String, Object> one : list){
			one.put("cover", SomeUtil.getHost(String.valueOf(one.get("cover"))));
		}
		
		JSONObject jRet = new JSONObject(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				this.put("list", list);
			}
		};
		
		return success(jRet);
	}
	
	@RequestMapping(value="dels", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<?> dels(
			@RequestParam(required = true) String ids,
			@RequestParam(required = true) String memberId){
		
		if (StrUtil.isEmpty(ids) || StrUtil.isEmpty(memberId)){
			return fail("ids or memberId or deviceId is empty");
		}
		
		logger.info("mbrcache.del>>>enter,recive data="+ids+","+memberId);
		
		Map<String, Object> eq = new HashMap<String, Object>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				this.put("memberId", memberId);
			}
		};
		List<Object> list = new ArrayList<Object>(){
			/**
			 * 
			 */
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
}
