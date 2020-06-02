package org.crow.movie.user.web.api.member;

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

/**
 * 
 * @author chenn
 *
 */
@RestController
@RequestMapping("/mbrsave")
public class MemberSaveApi extends BaseAdminController{

	@Autowired
	private MemberInfoService memberInfoService;
	
	/**
	 * 搜索统计
	 * @param request
	 * @param allParams
	 * @return
	 */
	@RequestMapping(value="search-count", method=RequestMethod.POST)
	public ReturnT<?> searchCount(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams){

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
