package org.crow.movie.user.web.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * 权限限制
 * @author chenn
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermessionLimit {

	/**
	 * 登陆拦截 (默认拦截)
	 */
	boolean limit() default true;
	
	/**
	 * 管理员拦截
	 */
	boolean manager() default true;
}
