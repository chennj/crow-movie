package org.crow.movie.user.web.api.member;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.db.entity.MemberComment;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberCommentService;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.web.controller.BaseAdminController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

/**
 * 评论点赞
 * @author chenn
 *
 */
@RestController
@RequestMapping("/mbrcomment")
public class MemberCommentApi extends BaseAdminController{

	@Autowired
	private MemberCommentService memberCommentService;
	
	/**
	 * 搜索统计
	 * @param request
	 * @param allParams
	 * @return
	 */
	@RequestMapping(value="search-count", method=RequestMethod.POST)
	public ReturnT<?> searchCount(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams){

		logger.info("mbrcomment.search>>>enter,recive data="+allParams.entrySet());
		
		Map<String, List<Map<String, Object>>> allMap 	= memberCommentService.search(
				Integer.valueOf(allParams.getOrDefault("page", 1).toString()), 
				Integer.valueOf(allParams.getOrDefault("pageSize", 20).toString()), 
				allParams);
		
		List<Map<String, Object>> list = allMap.get("list");
		
		Object fid = allParams.getOrDefault("fid", 0);
		
		for (Map<String, Object> one : list){
			one.put("reply_num", memberCommentService.count("fid", fid));
		}
		
		JSONObject jRet = new JSONObject(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				this.put("list", list);
				this.put("condition", allParams);
				this.put("fid", fid);
				this.put("movie_id", allParams.get("movie_id"));
			}
		};		
		return success(jRet);
	}

	@RequestMapping(value="del", method=RequestMethod.POST)
	public ReturnT<?> del(@RequestParam(required = true) Integer id){
		
		if (StrUtil.isEmpty(id)){
			return fail("没有id");
		}
		
		MemberComment entity = memberCommentService.getById(id);
		if (entity == null){
			return fail("评论不存在");
		}
		memberCommentService.delCascade(entity);
		
		return success("操作完成");
	}
}
