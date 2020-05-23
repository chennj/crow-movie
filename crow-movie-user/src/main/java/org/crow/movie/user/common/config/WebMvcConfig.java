package org.crow.movie.user.common.config;

import javax.annotation.Resource;

import org.crow.movie.user.web.interceptor.CookieInterceptor;
import org.crow.movie.user.web.interceptor.ManagerPermissionInterceptor;
import org.crow.movie.user.web.interceptor.MemberPermissionInterceptor;
import org.crow.movie.user.web.interceptor.TokenInterceptor;
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
    private MemberPermissionInterceptor memberInterceptor;
    @Resource
    private CookieInterceptor cookieInterceptor;
    @Resource
    private ManagerPermissionInterceptor managerInterceptor;
    @Resource
    private TokenInterceptor tokenInterceptor;

    /**
     * addPathPatterns 添加拦截规则
     * excludePathPatterns 排除拦截规则
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	registry.addInterceptor(tokenInterceptor).addPathPatterns("/**");
    	registry.addInterceptor(managerInterceptor).addPathPatterns("/**");
        registry.addInterceptor(memberInterceptor).addPathPatterns("/**");
        //registry.addInterceptor(cookieInterceptor).addPathPatterns("/**");
    }

}
