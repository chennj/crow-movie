package org.crow.movie.user.web.api.member.outside;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.entity.MemberPromo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberPromoService;
import org.crow.movie.user.common.util.Php2JavaUtil;
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
@RequestMapping("/public/mbrpromo")
@Permission(managerLimit=false)
public class MemberPromoPublicApi extends BasePublicController{

	@Autowired
	MemberPromoService memberPromoService;
	
	@RequestMapping(value="action", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<?> action(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams){
		logger.info("public.mbrpromo.action>>>enter,recive data="+allParams.entrySet());
		
		MemberPromo entity = new MemberPromo();
		// 未完待续
		return null;
	}
	
	/**
	 * 宣传片列表
	 * @param request
	 * @param allParams
	 * @return
	 */
	@RequestMapping(value="list", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<?> list(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams){
		
		logger.info("public.mbrpromo.list>>>enter,recive data="+allParams.entrySet());
		
		MemberInfo member = this.getUser();
		allParams.put("id", member.getId());
		List<Map<String, Object>> allList = memberPromoService.promoList(
				Integer.valueOf(allParams.getOrDefault("page", 1).toString()), 
				Integer.valueOf(allParams.getOrDefault("pageSize", 20).toString()), 
				allParams);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (Map<String, Object> one : allList){
			one.put("create_time", sdf.format(Php2JavaUtil.transTimeP2J(String.valueOf(one.get("create_time")))));
		}
		
		return success(allList);
	}
}
