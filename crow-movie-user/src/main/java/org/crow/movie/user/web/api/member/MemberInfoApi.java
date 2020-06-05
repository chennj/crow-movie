package org.crow.movie.user.web.api.member;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.constant.Const;
import org.crow.movie.user.common.db.entity.AppLevel;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.model.BaseTypeWrapper;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.AppLevelService;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.util.DigestUtils;
import org.crow.movie.user.common.util.Php2JavaUtil;
import org.crow.movie.user.common.util.CommUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.web.controller.BaseAdminController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 用户信息
 * @author chenn
 *
 */
@RestController
@RequestMapping("/mbrinfo")
@Api(tags = "User Info Related Interface Of Management",description="后台用户信息相关接口,需要token")
public class MemberInfoApi  extends BaseAdminController{

	@Autowired
	private MemberInfoService memberInfoService;
	
	@Autowired
	private AppLevelService appLevelService;
	
	/**
	 * 搜索-统计
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
		@ApiImplicitParam(name="id",value="用户id",required=false,paramType="query"),
		@ApiImplicitParam(name="account",value="用户账号",required=false,paramType="query"),
		@ApiImplicitParam(name="mobile",value="手机号",required=false,paramType="query"),
		@ApiImplicitParam(name="level",value="级别",required=false,paramType="query"),
		@ApiImplicitParam(name="is_visitor",value="是否游客",required=false,paramType="query"),
		@ApiImplicitParam(name="sex",value="性别",required=false,paramType="query"),
		@ApiImplicitParam(name="status",value="状态",required=false,paramType="query"),
		@ApiImplicitParam(name="reg_promo_code",value="推广吗",required=false,paramType="query"),
		@ApiImplicitParam(name="promo_code",value="推广吗",required=false,paramType="query"),
		@ApiImplicitParam(name="is_vip",value="是否vip",required=false,paramType="query"),
		@ApiImplicitParam(name="device_id_isnull",value="设备号是否是空",required=false,paramType="query"),
		@ApiImplicitParam(name="promo_num",value="推广数量",required=false,paramType="query"),
		@ApiImplicitParam(name="begin_time",value="开始时间",required=false,paramType="query"),
		@ApiImplicitParam(name="end_time",value="结束时间",required=false,paramType="query")
	})
	@RequestMapping(value="search-count", method=RequestMethod.POST)
	public ReturnT<?> searchCount(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams) throws NumberFormatException, ParseException{
		
		logger.info("mbrinfo.search>>>enter,recive data="+allParams.entrySet());
		
		BaseTypeWrapper<Integer> listCount = new BaseTypeWrapper<Integer>();
		
		Map<String, List<Map<String, Object>>> allMap 	= memberInfoService.search(
				Integer.valueOf(allParams.getOrDefault("page", 1).toString()), 
				Integer.valueOf(allParams.getOrDefault("pageSize", 50).toString()), 
				allParams, listCount);
		
		List<Map<String, Object>> loginTypeList 		= allMap.get("login_type_list");
		List<Map<String, Object>> todayLoginTyleList 	= allMap.get("today_login_type_list");
		List<Map<String, Object>> list 					= allMap.get("list");
		List<Map<String, Object>> visitorList			= allMap.get("visitor_list");
		List<Map<String, Object>> classList				= allMap.get("class_list");
		
		HashMap<String, Integer> sumMap = new HashMap<String, Integer>();
		
		Integer 
		sum_today_app = 0,
		sum_today_pc = 0;
		for (Map<String, Object> one : todayLoginTyleList){
			if (
					"ANDROID".equalsIgnoreCase(String.valueOf(one.get("device_type"))) ||
					"IOS".equalsIgnoreCase(String.valueOf(one.get("device_type"))) ){
				sum_today_app += Integer.valueOf(String.valueOf(one.get("count")));
			} else if ("PC".equalsIgnoreCase(String.valueOf(one.get("device_type")))){
				sum_today_pc += Integer.valueOf(String.valueOf(one.get("count")));
			}
		}
		sumMap.put("today_app", sum_today_pc);
		sumMap.put("today_pc", sum_today_pc);
		
		Integer 
		sum_app = 0,
		sum_pc = 0;
		for (Map<String, Object> one : loginTypeList){
			if (
					"ANDROID".equalsIgnoreCase(String.valueOf(one.get("device_type"))) ||
					"IOS".equalsIgnoreCase(String.valueOf(one.get("device_type"))) ){
				sum_app += Integer.valueOf(String.valueOf(one.get("count")));
			} else if ("PC".equalsIgnoreCase(String.valueOf(one.get("device_type")))){
				sum_pc += Integer.valueOf(String.valueOf(one.get("count")));
			}
		}
		sumMap.put("today_app", sum_app);
		sumMap.put("today_pc", sum_pc);
		
		Integer
		sum_today_visitor = 0;
		for (Map<String, Object> one : visitorList){
			sum_today_visitor += Integer.valueOf(String.valueOf(one.get("visitor_count")));
		}
		sumMap.put("today_visitor", sum_today_visitor);
		
		Integer
		sum_other = Integer.valueOf(String.valueOf(listCount.getT())) - sum_app - sum_pc;
		sumMap.put("other", sum_other);
		
		JSONObject jRet = new JSONObject(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				this.put("list", list);
				this.put("class_list", classList);
				this.put("condition", allParams);
				this.put("sum", sumMap);
			}
		};
		
		return success(jRet);
	}
	
	/**
	 * 改密码
	 * @param request
	 * @param password
	 * @param password2
	 * @return
	 */
	@ApiOperation(value = "修改用户密码",notes="修改用户密码")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="change-password", method=RequestMethod.POST)
	public ReturnT<?> changPwd(
			HttpServletRequest request,
			@RequestParam(required = true) Integer memberid,
			@RequestParam(required = true) String password){
		
		// param
		if (StrUtil.isEmpty(password) || StrUtil.isEmpty(memberid)){
			return fail("用户和密码不能为空");
		}

		
		MemberInfo mbr = memberInfoService.getById(memberid);
		if (null == mbr){
			return fail("用户不存在");
		}
		
		String pwd = DigestUtils.encryptMd5(password);
		mbr.setPassword(pwd);
		mbr.setCreateIp(getIp(request));
		mbr = memberInfoService.modify(mbr);
		
		return success(mbr);
	}
	
	/**
	 * 编辑用户信息
	 * @param request
	 * @param data
	 * @return
	 */
	@ApiOperation(value = "编辑用户信息",notes="编辑用户信息")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="allParams",value="文档缺陷，不需要填写",required=false,paramType="query"),
		@ApiImplicitParam(name="memberid",value="用户id",required=true,paramType="query"),
		@ApiImplicitParam(name="isNickName",value="其他需要修改得字段使用驼峰格式(eg:is_nick_name=>isNickName)",required=false,paramType="query")
	})
	@RequestMapping(value="edit", method=RequestMethod.POST)
	public ReturnT<?> edit(
			HttpServletRequest request,
			@RequestParam Map<String,Object> allParams){
		
		logger.info("mbrinfo.edit>>>enter,recive data="+allParams.entrySet());
		
		// param
		if (allParams.isEmpty()){
			return fail("data is empty");
		}
				
		JSONObject jo = new JSONObject(allParams);
		if (StrUtil.isEmpty(jo.getInteger("memberid"))){
			return fail("没有用户ID");
		}
		
		MemberInfo entity = memberInfoService.getById(jo.getInteger("memberid"));
		if (null == entity){
			return fail("member is not exists");
		}
		
		try {
			CommUtil.updateBean(entity, jo, Const.MEMBERINFO_FIELD_EDIT_IGNORE);
		} catch (Exception e) {
			logger.error("mbrinfo.edit>>>updateBean failed,"+e.getMessage());
			return fail("修改失败");
		}
		
		entity.setUpdateTime(Php2JavaUtil.transTimeJ2P(System.currentTimeMillis()));
		entity.setUpdateUser(this.getAdminId(request));
		memberInfoService.modify(entity);
		
		return success();
	}
	
	/**
	 * 新增用户
	 * @param request
	 * @param data
	 * @return
	 */
	@ApiOperation(value = "新增用户",notes="新增用户")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header"),
		@ApiImplicitParam(name="allParams",value="文档缺陷，不需要填写",required=false,paramType="query"),
		@ApiImplicitParam(name="isNickName",value="其他需要修改得字段使用驼峰格式(eg:is_nick_name=>isNickName)",required=false,paramType="query")
	})
	@RequestMapping(value="add", method=RequestMethod.POST)
	public ReturnT<?> add(
			HttpServletRequest request,
			@RequestParam Map<String,Object> allParams){
		
		logger.info("mbrinfo.add>>>enter,recive data="+allParams.entrySet());
		
		if (allParams.isEmpty()){
			logger.error("mbrinfo.add>>>数据的没有，搞啥");
			return fail("data is empty");
		}
		
		JSONObject jdata = null;
		MemberInfo mbr = null;
		String account;
		String password,password2;
		try {
			jdata 		= new JSONObject(allParams);
			account 	= jdata.getString("account");
			password 	= jdata.getString("password");
			password2 	= jdata.getString("password2");
			if (StrUtil.isEmpty(account) || StrUtil.isEmpty(password) || StrUtil.isEmpty(password2)){
				return fail("请输入名称，密码");
			}
			if (!password2.endsWith(password)){
				return fail("两次密码不一致");
			}
		} catch (Exception e){
			logger.error("mbrinfo.add>>>"+e.getMessage());
			return new ReturnT<String>(500, "data trans json error");
		}
		
		mbr = memberInfoService.getUnique("account", account);
		if (null != mbr){
			return fail("用户已存在");
		}
		
		mbr = JSON.parseObject(jdata.toJSONString(), new TypeReference<MemberInfo>(){});
		if (null == mbr){
			logger.error("mbrinfo.add>>>data trans bean exception");
			return fail("data trans bean exception");
		}
		
		mbr.setNickName(account);
		mbr.setIsVisitor(2);
		mbr.setPassword(DigestUtils.encryptPwd(password));
		mbr.setToken("");
		mbr.setCreateDate(new Date());
		mbr.setCreateTime(now());
		mbr.setUpdateTime(now());
		mbr.setCreateUser(this.getAdminId(request));

		memberInfoService.add(mbr);
		
		return success();
		
	}
	
	/**
	 * 删除用户
	 */
	@ApiOperation(value = "删除用户",notes="删除用户")
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
		
		MemberInfo entity = memberInfoService.getById(id);
		if (null == entity){
			return fail("member is not exists");
		}

		memberInfoService.delete(id);
		
		return success("删除完成");
	}
	
	/**
	 * 变更用户状态（status）
	 * if 1 then 2 else 1
	 * @param request
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "变更用户状态",notes="变更用户状态")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="status", method=RequestMethod.POST)
	public ReturnT<?> status(
			HttpServletRequest request,
			@RequestParam(required=true) Integer id){
	
		if (StrUtil.isEmpty(id)){
			return fail("id is empty");
		}

		MemberInfo entity = memberInfoService.getById(id);
		if (null == entity){
			return fail("用户不存在");
		}
		
		int n = memberInfoService.updateStatus(id);
		if (n>0)return success();
		else return fail();
	}
	
	/**
	 * 获取用户详细信息，及applevel
	 * @param request
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "用户详细信",notes="用户详细信")
	@ApiImplicitParams({
		@ApiImplicitParam(name="accessToken",value="访问token",required=true,paramType="header")
	})
	@RequestMapping(value="details", method=RequestMethod.POST)
	public ReturnT<?> details(
			HttpServletRequest request,
			@RequestParam(required=true) Integer id){
	
		if (StrUtil.isEmpty(id)){
			return fail("id is empty");
		}

		MemberInfo entity = memberInfoService.getById(id);
		if (null == entity){
			return fail("用户不存在");
		}
		
		List<AppLevel> list = appLevelService.getList("status", 1);
		
		JSONObject jRet = new JSONObject(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				this.put("res", entity);
				this.put("class_list", list);
			}
		};
		
		return success(jRet);
		
	}
}
