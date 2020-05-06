package org.crow.movie.user.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.crow.movie.user.common.constant.Const;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseController {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected MemberInfo getUserInfo(HttpServletRequest request){
		
		HttpSession session = request.getSession();
		return (MemberInfo) session.getAttribute(Const.SESSION_USER_INFO_KEY);
	}
}
