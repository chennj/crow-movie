package org.crow.movie.user.web.api.member.outside;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.assertj.core.util.Arrays;
import org.crow.movie.user.common.constant.CC;
import org.crow.movie.user.common.db.Page;
import org.crow.movie.user.common.db.entity.MemberFeedback;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberFeedbackService;
import org.crow.movie.user.common.util.Php2JavaUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.web.annotation.Permission;
import org.crow.movie.user.web.controller.BasePublicController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@RestController
@RequestMapping("/public/mbrfeedback")
@Permission(managerLimit=false)
public class MemberFeedbackPublicApi extends BasePublicController{

	@Autowired
	private MemberFeedbackService memberFeedbackService;
	
	@RequestMapping(value="feedback-add", method=RequestMethod.POST)
	public ReturnT<?> add(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams,
			@RequestParam("file") MultipartFile file){
		
		logger.info("public.mbrfeedback.feedback-add receive data:"+allParams.entrySet());
		
		String feedback_type = allParams.get("feedback_type").toString();
		if (StrUtil.isEmpty(feedback_type)){
			return fail("反馈类型不能为空");
		}
		String content = allParams.get("content").toString();
		if (StrUtil.isEmpty(content)){
			return fail("反馈内容不能为空");
		}
		int contentLen = content.length();
		if (contentLen<10 || contentLen>100) {
			return fail("反馈内容长度有误");
        }
		
		MemberFeedback entity = new MemberFeedback();

		if (!file.isEmpty()) {
			//check file
			String[] fileExts = {"jpg","jpeg","png"};
			String filename = file.getOriginalFilename();
			if (!Arrays.asList(fileExts).contains(filename.substring(filename.lastIndexOf(".")))){
				return fail("文件格式不正确");
			}
			try {
	            // Get the file and save it somewhere
	            byte[] bytes = file.getBytes();
	            Path path = Paths.get(appProperties.getFeedback()+ File.separator + file.getOriginalFilename());
	            Files.write(path, bytes);
	            entity.setPic(file.getOriginalFilename());
	        } catch (IOException e) {
	            return fail(e.getMessage());
	        }
        }
		
		MemberInfo member = getUser();
		entity.setContent(content);
		entity.setMemberId(member.getId());
		entity.setFeedbackType(Integer.valueOf(feedback_type));
		entity.setCreateTime(Php2JavaUtil.transTimeJ2P(System.currentTimeMillis()));
		
		entity = memberFeedbackService.add(entity);
		if (entity != null){
			return success(entity);
		} else {
			return success("反馈失败");
		}
	}
	
	@RequestMapping(value="feedback", method=RequestMethod.POST)
	public ReturnT<?> add(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams){
		
		logger.info("public.mbrfeedback.feedback receive data:"+allParams.entrySet());
		
		Map<String, Object> eq = new HashMap<>();
		eq.put("memberId", this.getUser().getId());
		Page<MemberFeedback> page = memberFeedbackService.page(
				Integer.valueOf(allParams.getOrDefault("page", 1).toString()), 
				Integer.valueOf(allParams.getOrDefault("pageSize", 20).toString()),
				eq);
		
		Map<Integer,String> feedback_dic = CC.fbtMap();
		
		JSONArray jRet = new JSONArray();
		for (MemberFeedback one : page.getResults()){
			JSONObject jo = new JSONObject();
			jo.put("feedback_type", feedback_dic.get(one.getFeedbackType()));
			jo.put("create_time", this.ts(one.getCreateTime()));
			jRet.add(jo);
		}
		return success(jRet);
	}
	
	@RequestMapping(value="feedback-type", method=RequestMethod.POST)
	public ReturnT<?> feedbackType(){
		
		logger.info("public.mbrfeedback.feedback-type enter");
		
		return success(CC.FEEDBACK_TYPE_LIST);
	}
}
