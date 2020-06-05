package org.crow.movie.user.web.api.member.outside;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.db.entity.MemberSearch;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberSearchService;
import org.crow.movie.user.common.util.Php2JavaUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.web.annotation.Permission;
import org.crow.movie.user.web.controller.BasePublicController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/public/mbrsearch")
@Permission(managerLimit=false)
@Api(tags = "Search-Key Related Interface Of Mobile/Pc",description="手机/PC搜索关键字相关接口,需要token")
public class MemberSearchPublicApi extends BasePublicController{

	@Autowired
	private MemberSearchService memberSearchService;
	
	/**
	 * 新增搜索关键字
	 * @param request
	 * @param keywords
	 * @return
	 */
	@ApiOperation(value = "新增搜索关键字",notes="新增搜索关键字")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="add-keyword", method=RequestMethod.POST)
	public ReturnT<?> action(HttpServletRequest request,
			@RequestParam(required=true) String keywords){
		logger.info("public.mbrsearch.add-keyword>>>enter,recive data="+keywords);
		
		//valid
		if (StrUtil.isEmpty(keywords)){
			return fail("搜索关键词不能为空");
		}
		if (keywords.length() > 20){
			return fail("搜索关键词长度有误");
		}
		
		int count = memberSearchService.countNative("select count(1) from hg_member_search where member_id=?1 and keywords = ?2", this.getMemberId(request),keywords);
		if (count <= 0){
			MemberSearch entity = new MemberSearch();
			entity.setMemberId(this.getMemberId(request));
			entity.setKeywords(keywords);
			entity.setCreateTime(Php2JavaUtil.transTimeJ2P(System.currentTimeMillis()));
			memberSearchService.add(entity);
		}
		
		return success();
	}
	
	/**
	 * 返回用户关键词表
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "用户关键词表",notes="用户关键词表")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="keywords", method=RequestMethod.POST)
	public ReturnT<?> keywords(HttpServletRequest request){
		
		logger.info("public.mbrsearch.keywords>>>enter");
		
		List<MemberSearch> list = memberSearchService.getList("createTime desc", "memberId", this.getMemberId(request));
		List<String> keywords = new ArrayList<>();
		for (MemberSearch one : list){
			keywords.add(one.getKeywords());
		}
		return success(keywords);
	}
	
	/**
	 * 清理用户关键词表
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "删除用户关键词表",notes="删除用户关键词表")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="clear", method=RequestMethod.POST)
	public ReturnT<?> clear(HttpServletRequest request){
		
		logger.info("public.mbrsearch.clear>>>enter");
		
		int memberId = this.getMemberId(request);
		memberSearchService.del("memberId", memberId);
		return success();
	}
}
