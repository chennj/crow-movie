package org.crow.movie.user.web.interceptor;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web mvc config
 * @author chenn
 *
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private MemberPermissionInterceptor permissionInterceptor;
    @Resource
    private CookieInterceptor cookieInterceptor;
    @Resource
    private ManagerPermissionInterceptor securityInterceptor;

    /**
     * addPathPatterns 添加拦截规则
     * excludePathPatterns 排除拦截规则
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	registry.addInterceptor(securityInterceptor).addPathPatterns("/**");
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/**");
        registry.addInterceptor(cookieInterceptor).addPathPatterns("/**");
    }

}
