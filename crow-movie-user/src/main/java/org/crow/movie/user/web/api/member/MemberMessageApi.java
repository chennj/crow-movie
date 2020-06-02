package org.crow.movie.user.web.api.member;

import org.crow.movie.user.common.constant.CC;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.web.controller.BaseAdminController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 
 * @author chenn
 *
 */
@RestController
@RequestMapping("/mbrmessage")
public class MemberMessageApi extends BaseAdminController{	
	
	@RequestMapping(value="feedback-type", method=RequestMethod.POST)
	public ReturnT<?> feedbackType(){
		
		logger.info("attr.feedback-type>>>enter");
		
		return success(CC.FEEDBACK_TYPE_LIST);
	}
}
