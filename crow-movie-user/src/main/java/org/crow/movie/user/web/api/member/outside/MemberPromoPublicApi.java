package org.crow.movie.user.web.api.member.outside;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberPromoService;
import org.crow.movie.user.common.util.Php2JavaUtil;
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
@RequestMapping("/public/mbrpromo")
@Permission(managerLimit=false)
@Api(tags = "Promo Related Interface Of Mobile/Pc",description="手机/PC用户推荐记录相关接口,需要token")
public class MemberPromoPublicApi extends BasePublicController{

	@Autowired
	private MemberPromoService memberPromoService;
	
	/**
	 * 宣传片列表
	 * @param request
	 * @param allParams
	 * @return
	 */
	@ApiOperation(value = "宣传推荐列表",notes="宣传推荐列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="allParams",value="文档缺陷，不需要填写",required=false,paramType="query"),
		@ApiImplicitParam(name="page",value="开始页",required=false,paramType="query"),
		@ApiImplicitParam(name="pageSize",value="页尺寸",required=false,paramType="query")
	})
	@RequestMapping(value="list", method=RequestMethod.POST)
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
