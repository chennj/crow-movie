package org.crow.movie.user.web.interceptor;

import java.net.MalformedURLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.assertj.core.util.Arrays;
import org.crow.movie.user.common.ApplicationProperties;
import org.crow.movie.user.common.util.IPUtil;
import org.crow.movie.user.web.annotation.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class ManagerPermissionInterceptor extends HandlerInterceptorAdapter{

	protected static final Logger logger = LoggerFactory.getLogger(ManagerPermissionInterceptor.class.getClass());
	
	@Autowired
    private ApplicationProperties properties;
	
	// URL匹配器
    //private AntPathMatcher matcher = new AntPathMatcher();
    
	/**
	 * ip白名单
	 */
	@Value("${movie.user.ip}")
	private String ipAddr;
	
	/**
	 * referer 是否允许空
	 */
	@Value("${movie.user.allowRefererEmpty}")
	private String allowEmpty;
	
	private static List<Object> ipAddrs;

	/**
	 * 方法白名单
	 */
	@Value("${movie.user.excludeUrl}")
	private String excludeUrl;
	
	private static String[] excludeUrls;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		if (!(handler instanceof HandlerMethod)) {
			return super.preHandle(request, response, handler);
		}

		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=utf-8");		
		
		if (null == ipAddrs){
			ipAddrs = Arrays.asList(ipAddr.split(";"));
		}
		
		if (null == excludeUrls){
			excludeUrls = excludeUrl.split(",");
		}

		/**
		 * 在请求方法(requestMethod)白名单中不进行拦截(例如isAlive)
		 */		
		if (InterceptorFunc.IsUrlWhiteList(request, response, excludeUrls)){
			return true;
		}
        
		/**
		 * 如果是manager功能
		 */
		HandlerMethod method 	= (HandlerMethod)handler;
		Permission permission 	= method.getMethodAnnotation(Permission.class);
		if (permission == null || permission.managerLimit()){
			
			// 检查ip地址白名单
			boolean ajaxFlag 	= "XMLHttpRequest".equalsIgnoreCase(request.getHeader("x-requested-with"));
			String clientIp 	= IPUtil.getClientIp(request);
			if (!ipAddrs.contains(clientIp)){
				
				return InterceptorFunc.FAIL(response,"{\"code\":\"404\",\"msg\":\"ip:"+clientIp+" 不在白名单内\"}");
			}

			// referer 拦截器防御CSRF攻击
			// 小知识：
			// 如下：其中Web A为存在CSRF漏洞的网站，Web B为攻击者构建的恶意网站，User C为Web A网站的合法用户。
			// 首先用户C浏览并登录了受信任站点A；
			// 登录信息验证通过以后，站点A会在返回给浏览器的信息中带上已登录的cookie，cookie信息会在浏览器端保存一定时间（根据服务端设置而定）；
			// 完成这一步以后，用户在没有登出（清除站点A的cookie）站点A的情况下，访问恶意站点B；
			// 这时恶意站点 B的某个页面向站点A发起请求，而这个请求会带上浏览器端所保存的站点 A 的cookie；
			// 站点A根据请求所带的 cookie，判断此请求为用户C所发送的。
			// 因此，站点A会报据用户C的权限来处理恶意站点B所发起的请求，而这个请求可能以用户C的身份发送 邮件、短信、消息，
			// 以及进行转账支付等操作，这样恶意站点B就达到了伪造用户C请求站点 A的目的。
			// 受害者只需要做下面两件事情，攻击者就能够完成CSRF攻击：
			//
			// 登录受信任站点 A，并在本地生成cookie；
			// 在不登出站点A（清除站点A的cookie）的情况下，访问恶意站点B。
			
			String referer 	= request.getHeader("Referer");
			String host 	= request.getServerName();
			if (referer == null){
				if (!"yes".equalsIgnoreCase(allowEmpty)){
					if (ajaxFlag){
						response.setContentType("application/json;charset=utf-8");
					}
					return InterceptorFunc.FAIL(response,"{\"code\":\"404\",\"msg\":\"不能在地址栏直接访问\"}");
				}
				return true;
			}
			
            java.net.URL url = null;
            try {
                url = new java.net.URL(referer);
            } catch (MalformedURLException e) {
                // URL解析异常，置为400，不返回有效信息
                return InterceptorFunc.FAIL(response,"{\"code\":\"404\",\"msg\":\"URL解析异常\"}");
            }
            // 首先判断请求域名和referer域名是否相同
            if (!host.equals(url.getHost())) {
                // 如果不等，判断是否在白名单中
                if (properties.getRefererDomain() != null) {
                    for (String s : properties.getRefererDomain()) {
                        if (s.equals(referer)) {
                            return true;
                        }
                    }
                }
                // URL解析异常，也置为400，不返回有效信息
                return InterceptorFunc.FAIL(response,"{\"code\":\"404\",\"msg\":\"拒绝访问\"}");
           }

		}
		
		return true;
	}

}
