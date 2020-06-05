package org.crow.movie.user.web.api.member;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberCacheService;
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
@RequestMapping("/mbrcache")
@Api(tags = "User Cache Related Interface Of Management",description="后台用户缓存相关接口,需要token")
public class MemberCacheApi extends BaseAdminController{

	@Autowired
	private MemberCacheService memberCacheService;
	
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
		@ApiImplicitParam(name="begin_time",value="开始时间",required=false,paramType="query"),
		@ApiImplicitParam(name="end_time",value="结束时间",required=false,paramType="query")
	})
	@RequestMapping(value="search-count", method=RequestMethod.POST)
	public ReturnT<?> searchCount(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams) throws NumberFormatException, ParseException{

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
	@ApiOperation(value = "用户缓存",notes="用户缓存")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="allParams",value="文档缺陷，不需要填写",required=false,paramType="query"),
		@ApiImplicitParam(name="page",value="开始页",required=false,paramType="query"),
		@ApiImplicitParam(name="pageSize",value="页尺寸",required=false,paramType="query"),
		@ApiImplicitParam(name="member_id",value="用户ID",required=false,paramType="query"),
		@ApiImplicitParam(name="device_id",value="用户设备号",required=false,paramType="query")
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

	@ApiOperation(value = "删除缓存",notes="删除缓存")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="ids",value="多个用','隔开",required=false,paramType="query")
	})
	@RequestMapping(value="dels", method=RequestMethod.POST)
	public ReturnT<?> dels(
			@RequestParam(required = true) String ids,
			@RequestParam(required = true) Integer memberid,
			@RequestParam(required = true) String deviceid){
		
		if (StrUtil.isEmpty(ids) || StrUtil.isEmpty(memberid) || StrUtil.isEmpty(deviceid)){
			return fail("ids or memberId or deviceId is empty");
		}
		
		logger.info("mbrcache.del>>>enter,recive data="+ids+","+memberid+","+deviceid);
		
		String[] idss		= ids.split(",");
		
		List<Object> list = new ArrayList<Object>();
		for (String s : idss){
			list.add(Integer.valueOf(s));
		}
		logger.info("mbrcache.del>>>enter,recive data="+ids+","+memberid+","+deviceid);
		
		Map<String, Object> eq = new HashMap<String, Object>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				this.put("memberId", memberid);
				this.put("deviceId", deviceid);
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
