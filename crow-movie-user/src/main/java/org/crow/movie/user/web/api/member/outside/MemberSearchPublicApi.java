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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author chenn
 *
 */
@Controller
@RequestMapping("/public/mbrsearch")
@Permission(managerLimit=false)
public class MemberSearchPublicApi extends BasePublicController{

	@Autowired
	MemberSearchService memberSearchService;
	
	/**
	 * 新增搜索关键字
	 * @param request
	 * @param keywords
	 * @return
	 */
	@RequestMapping(value="add-keyword", method=RequestMethod.POST)
	@ResponseBody
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
	@RequestMapping(value="keywords", method=RequestMethod.POST)
	@ResponseBody
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
	@RequestMapping(value="clear", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<?> clear(HttpServletRequest request){
		
		logger.info("public.mbrsearch.clear>>>enter");
		
		int memberId = this.getMemberId(request);
		memberSearchService.del("memberId", memberId);
		return success();
	}
}
