package org.crow.movie.user.web.api.member.outside;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.assertj.core.util.Arrays;
import org.crow.movie.user.common.constant.Const;
import org.crow.movie.user.common.db.entity.MemberFeedback;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberFeedbackService;
import org.crow.movie.user.common.util.Php2JavaUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.web.annotation.Permission;
import org.crow.movie.user.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/mbrfeedback/public")
@Permission(managerLimit=false)
public class MemberFeedbackApi extends BaseController{

	@Autowired
	MemberFeedbackService memberFeedbackService;
	
	@RequestMapping(value="add", method=RequestMethod.POST)
	@ResponseBody
	public ReturnT<?> add(HttpServletRequest request,
			@RequestParam Map<String,Object> allParams,
			@RequestParam("file") MultipartFile file){
		
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
	            Path path = Paths.get(Const.FILE_UPLOADED_FOLDER + file.getOriginalFilename());
	            Files.write(path, bytes);
	            entity.setPic(Const.FILE_UPLOADED_FOLDER + file.getOriginalFilename());
	        } catch (IOException e) {
	            return fail(e.getMessage());
	        }
        }
		
		MemberInfo member = getUserInfo(request);
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
	
}
