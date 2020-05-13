package org.crow.movie.user.common.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationUtil implements ApplicationContextAware{

	private static ApplicationContext applicationContext = null;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		
		ApplicationUtil.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 获得Bean
	 * 
	 * @param beanName
	 * @return
	 */
	public static Object getBean(String beanName) {
		ApplicationContext context = getApplicationContext();
		return context.getBean(beanName);
	}

	/**
	 * 获得Bean
	 * 
	 * @param beanName
	 * @param cls
	 * @return
	 */
	public static <T> T getBean(String beanName, Class<T> cls) {
		ApplicationContext context = getApplicationContext();
		return context.getBean(beanName, cls);
	}

	/**
	 * 获取Bean（以class的name为ID获取）
	 * 
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> cls) {
		return (T) applicationContext.getBean(upperToLowerCamelCase(cls.getSimpleName()));
	}

	/**
	 * 拷贝属性，从来源到目标
	 * 
	 * @param source
	 * @param target
	 */
	public static void copyProperties(Object source, Object target) {
		BeanUtils.copyProperties(source, target);
	}

	/**
	 * 拷贝属性，从来源到目标
	 * 
	 * @param source
	 * @param target
	 */
	public static void copyProperties(Object source, Object target,
			String[] ignoreProperties) {
		BeanUtils.copyProperties(source, target, ignoreProperties);
	}

	/**
	 * 将对象转换为对应的类型
	 * 
	 * @param obj
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convertObjectToTargetClass(Object obj, Class<T> cls) {
		return (T) obj;
	}
	
    private static String upperToLowerCamelCase(String upperCamelCase) {
        char[] cs = upperCamelCase.toCharArray();
        // 类名首字母小写
        cs[0] += 32;
        return String.valueOf(cs);
    }
}
