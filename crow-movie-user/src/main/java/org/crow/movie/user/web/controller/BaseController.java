package org.crow.movie.user.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.crow.movie.user.common.constant.Const;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseController {

	@Value("${movie.user.salt}")
	protected String salt;

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected MemberInfo getUserInfo(HttpServletRequest request){
		
		HttpSession session = request.getSession();
		return (MemberInfo) session.getAttribute(Const.SESSION_USER_INFO_KEY);
	}
}
