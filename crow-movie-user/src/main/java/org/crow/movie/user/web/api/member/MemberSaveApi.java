package org.crow.movie.user.web.api.member;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberInfoService;
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

/**
 * 
 * @author chenn
 *
 */
@RestController
@RequestMapping("/mbrsave")
@Api(tags = "User Save Related Interface Of Management",description="后台用户保存记录相关接口,需要token")
public class MemberSaveApi extends BaseAdminController{

	@Autowired
	private MemberInfoService memberInfoService;
	
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
		@ApiImplicitParam(name="is_save_qrcode",value="是由有二维码",required=false,paramType="query"),
		@ApiImplicitParam(name="begin_time",value="开始时间",required=false,paramType="query"),
		@ApiImplicitParam(name="end_time",value="结束时间",required=false,paramType="query")
	})
	@RequestMapping(value="search-count", method=RequestMethod.POST)
	public ReturnT<?> searchCount(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams) throws NumberFormatException, ParseException{

		logger.info("mbrsave.search>>>enter,recive data="+allParams.entrySet());
		
		Map<String, List<Map<String, Object>>> allMap 	= memberInfoService.getSave(
				Integer.valueOf(allParams.getOrDefault("page", 1).toString()), 
				Integer.valueOf(allParams.getOrDefault("pageSize", 20).toString()), 
				allParams);
		
		
		JSONObject jRet = new JSONObject(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				this.put("list", allMap.get("list"));
				this.put("condition", allParams);
			}
		};
		
		return success(jRet);
	}

}
