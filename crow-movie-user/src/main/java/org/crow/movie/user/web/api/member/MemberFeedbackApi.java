package org.crow.movie.user.web.api.member;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.constant.CC;
import org.crow.movie.user.common.db.entity.MemberFeedback;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberFeedbackService;
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
public class MemberFeedbackApi extends BaseController{

	@Autowired
	MemberFeedbackService memberFeedbackService;
	
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
		
		Map<String, List<Map<String, Object>>> allMap 	= memberFeedbackService.search(
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
				this.put("class_list", CC.FEEDBACK_TYPE);
				this.put("condition", allParams);
			}
		};
		
		return success(jRet);
	}
	
	@RequestMapping(value="status", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<?> status(@RequestParam(required = true) Integer id){
		
		if (StrUtil.isEmpty(id)){
			return fail("没有id");
		}
		
		MemberFeedback entity = memberFeedbackService.getById(id);
		if (null == entity){
			return fail("消息不存在");
		}
		if (entity.getAdminIsRead().equals(1)){
			entity.setAdminIsRead(2);
		} else {
			entity.setAdminIsRead(1);
		}
		memberFeedbackService.modify(entity);
		return success("操作完成");
	}
}
