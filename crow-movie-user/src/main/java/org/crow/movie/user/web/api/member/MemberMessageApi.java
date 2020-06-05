package org.crow.movie.user.web.api.member;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.constant.CC;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.entity.MemberMessage;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.db.service.MemberMessageService;
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


/**
 * 
 * @author chenn
 *
 */
@RestController
@RequestMapping("/mbrmessage")
@Api(tags = "User Message Type Related Interface Of Management",description="后台用户消息列表相关接口,需要token")
public class MemberMessageApi extends BaseAdminController{	

	@Autowired
	private MemberMessageService memberMessageService;
	
	@Autowired
	private MemberInfoService memberInfoService;
	
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
		
		Map<String, List<Map<String, Object>>> allMap 	= memberMessageService.search(
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

	@ApiOperation(value = "新增消息",notes="新增消息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
	})
	@RequestMapping(value="add", method=RequestMethod.POST)
	public ReturnT<?> add(
			HttpServletRequest request,
			@RequestParam(required=true) Integer memberid,
			@RequestParam(required=true) String title,
			@RequestParam(required=true) String content){
		
		if (StrUtil.isEmpty(title) || StrUtil.isEmpty(content) || StrUtil.isEmpty(memberid)){
			return fail("id title content is empty");
		}
		
		MemberInfo mbr = memberInfoService.getById(memberid);
		if (null == mbr){
			return fail("用户不存在");
		}
		
		MemberMessage mm = new MemberMessage();
		mm.setMemberId(memberid);
		mm.setMessageType(1);
		mm.setTitle(title);
		mm.setContent(content);
		mm.setCreateTime(now());
		mm.setCreateUser(this.getAdminId(request));

		memberMessageService.add(mm);
		
		return success();
		
	}
	
	@ApiOperation(value = "编辑用户消息",notes="编辑用户消息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="id",value="消息id",required=true,paramType="query")
	})
	@RequestMapping(value="edit", method=RequestMethod.POST)
	public ReturnT<?> edit(
			HttpServletRequest request,
			@RequestParam(required=true) Integer messageid,
			@RequestParam(required=true) String title,
			@RequestParam(required=true) String content){
		
		if (StrUtil.isEmpty(title) || StrUtil.isEmpty(content) || StrUtil.isEmpty(messageid)){
			return fail("id title content is empty");
		}

		MemberMessage mm = memberMessageService.getById(messageid);
		if (null == mm){
			return fail("消息不存在");
		}
		MemberInfo mbr = memberInfoService.getById(mm.getMemberId());
		if (null == mbr){
			return fail("用户不存在");
		}

		mm.setTitle(title);
		mm.setContent(content);
		mm.setUpdateTime(now());
		mm.setUpdateUser(this.getAdminId(request));
		memberMessageService.modify(mm);
		
		return success();
	}
	
	@ApiOperation(value = "删除",notes="删除")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="del", method=RequestMethod.POST)
	public ReturnT<?> del(
			HttpServletRequest request,
			@RequestParam(required=true) Integer id){
		
		if (StrUtil.isEmpty(id)){
			return fail("id is empty");
		}
		
		MemberMessage entity = memberMessageService.getById(id);
		if (null == entity){
			return fail("消息不存在");
		}

		memberMessageService.del(id);
		
		return success("删除完成");
	}
	
	@ApiOperation(value = "用户消息类型",notes="用户消息类型")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="feedback-type", method=RequestMethod.POST)
	public ReturnT<?> feedbackType(){
		
		logger.info("attr.feedback-type>>>enter");
		
		return success(CC.FEEDBACK_TYPE_LIST);
	}
}
