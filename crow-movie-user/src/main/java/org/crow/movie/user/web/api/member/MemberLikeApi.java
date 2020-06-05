package org.crow.movie.user.web.api.member;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.assertj.core.util.Arrays;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberLikeService;
import org.crow.movie.user.common.util.CommUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.web.controller.BaseAdminController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/mbrlike")
@Api(tags = "User Like Related Interface Of Management",description="后台用户爱好相关接口,需要token")
public class MemberLikeApi extends BaseAdminController{

	@Autowired
	private MemberLikeService memberLikeService;
	
	/**
	 * 搜索统计
	 * @param request
	 * @param allParams
	 * @return
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	@ApiOperation(value = "搜索统计",notes="搜索统计")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="allParams",value="文档缺陷，不需要填写",required=false,paramType="query"),
		@ApiImplicitParam(name="page",value="开始页",required=false,paramType="query"),
		@ApiImplicitParam(name="pageSize",value="页尺寸",required=false,paramType="query"),
		@ApiImplicitParam(name="account",value="用户账号",required=false,paramType="query"),
		@ApiImplicitParam(name="title",value="影片标题",required=false,paramType="query"),
		@ApiImplicitParam(name="is_visitor",value="是否游客",required=false,paramType="query"),
		@ApiImplicitParam(name="movie_id",value="影片id",required=false,paramType="query"),
		@ApiImplicitParam(name="begin_time",value="开始时间",required=false,paramType="query"),
		@ApiImplicitParam(name="end_time",value="结束时间",required=false,paramType="query")
	})
	@RequestMapping(value="search-count", method=RequestMethod.POST)
	public ReturnT<?> searchCount(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams) throws NumberFormatException, ParseException{

		logger.info("mbrlike.search>>>enter,recive data="+allParams.entrySet());
		
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

	@ApiOperation(value = "搜索统计",notes="搜索统计")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="like", method=RequestMethod.POST)
	public ReturnT<?> like(
			@RequestParam(required = true) Integer memberId,
			@RequestParam(required = false,defaultValue = "1") Integer page,
			@RequestParam(required = false,defaultValue = "20") Integer pageSize){
		
		List<Map<String, Object>> list = memberLikeService.like(page,pageSize,memberId);
		
		for (Map<String, Object> one : list){
			one.put("cover", CommUtil.getHost(String.valueOf(one.get("cover"))));
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
	
	@ApiOperation(value = "删除缓存",notes="删除缓存")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="ids",value="多个用','隔开",required=false,paramType="query")
	})
	@RequestMapping(value="dels", method=RequestMethod.POST)
	public ReturnT<?> dels(
			@RequestParam(required = true) String ids,
			@RequestParam(required = true) Integer memberid){
		
		if (StrUtil.isEmpty(ids) || StrUtil.isEmpty(memberid)){
			return fail("ids or memberId or deviceId is empty");
		}
		
		logger.info("mbrlike.del>>>enter,recive data="+ids+","+memberid);
		
		Map<String, Object> eq = new HashMap<String, Object>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				this.put("memberId", memberid);
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
