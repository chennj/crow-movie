package org.crow.movie.user.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.crow.movie.user.common.db.entity.AdminInfo;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.AdminInfoService;
import org.crow.movie.user.common.db.service.MemberInfoService;
import org.crow.movie.user.common.util.MapUtil;
import org.crow.movie.user.common.util.Php2JavaUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.crow.movie.user.common.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONObject;

public abstract class BaseController {

	@Autowired
	private MemberInfoService memberInfoService;
	@Autowired
	private AdminInfoService adminInfoService;

	@Value("${movie.user.salt}")
	protected String salt;

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected int isVip = 2;
	
	/**
	 * 根据token获取member
	 * @param request
	 * @return
	 */
	protected MemberInfo getMemberInfo(HttpServletRequest request){
		
		//HttpSession session = request.getSession();
		//return (MemberInfo) session.getAttribute(Const.SESSION_USER_INFO_KEY);
		String token = request.getHeader("accessToken");
		Integer id = TokenUtil.getUserID(token);
		MemberInfo m = memberInfoService.getById(id);
		if (m.getExpireTime() > Php2JavaUtil.transTimeJ2P(System.currentTimeMillis())){
			isVip = 1;
		} else {
			m.setExpireTime(0);
		}
		return memberInfoService.getById(id);
	}
	
	protected JSONObject getMemberInfoJson(HttpServletRequest request){
		MemberInfo m = getMemberInfo(request);
		if (null != m){
			try {
				return new JSONObject(MapUtil.objectToMap1(m));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	protected int getMemberId(HttpServletRequest request){
		
		String token = request.getHeader("accessToken");
		return TokenUtil.getUserID(token);
	}
	
	/**
	 * 用于根据token获取admin用户
	 * @param request
	 * @return
	 */
	protected AdminInfo getAdminInfo(HttpServletRequest request){
		String token = request.getHeader("accessToken");
		Integer id = TokenUtil.getUserID(token);
		return adminInfoService.getById(id);
	}
	
	protected int getAdminId(HttpServletRequest request){
		
		String token = request.getHeader("accessToken");
		return TokenUtil.getUserID(token);
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
