package org.crow.movie.user.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.crow.movie.user.common.constant.Const;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.util.StrUtil;
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
	
	protected ReturnT<String> success(String msg){
		return new ReturnT<>(msg);
	}
	
	protected  ReturnT<String> success(){
		return new ReturnT<>("operator success");
	}
	
	protected  ReturnT<?> success(Object t){
		return new ReturnT<>(t);
	}
	
	protected  <T> ReturnT<T> fail(String msg){
		return new ReturnT<>(ReturnT.FAIL_CODE,msg);
	}
	
	protected  <T> ReturnT<T> fail(){
		return new ReturnT<>(ReturnT.FAIL_CODE,"operator failed");
	}
	
	protected MemberInfo getMemberInfo(HttpServletRequest request){
		
		return (MemberInfo)request.getSession().getAttribute(Const.SESSION_USER_INFO_KEY);
	}
	
	protected String getIp(HttpServletRequest request){
		
		try {
			String ip = request.getHeader("X-Real-IP");
			if (!StrUtil.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)){
				return ip;
			}
			ip = request.getHeader("X-Forwarded-For");
			if (!StrUtil.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)){
				//多次反向代理会有多个ip，第一个为真实。
				int index = ip.indexOf(',');
				if (index != -1){
					return ip.substring(0, index);
				} else {
					return ip;
				}
			} else {
				return request.getRemoteAddr();
			}
		} catch (Exception e){
			return "null";
		}
	}
}
