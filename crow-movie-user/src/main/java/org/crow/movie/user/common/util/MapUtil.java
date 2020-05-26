package org.crow.movie.user.common.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapUtil{

	private static final Logger logger = LoggerFactory.getLogger(MapUtil.class);
	
	public static <V,T> Map<V, T> listToMap(String methodNameOfFiled, List<T> list){
		
		Map<V, T> resultMap = new HashMap<>();
		try {
			for (T t : list){
				Method m = t.getClass().getDeclaredMethod(methodNameOfFiled, new Class<?>[]{});
				if (null == m){
					throw new Exception("Method "+methodNameOfFiled+" not found");
				}
				@SuppressWarnings("unchecked")
				V key = (V) m.invoke(t, new Object[]{});
				resultMap.put(key, t);
			}
		} catch(Exception e){
			logger.error("listToMap>>>"+e.getLocalizedMessage());
			e.printStackTrace();
		}
		return resultMap;
	}
	
	public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {    
        if (map == null)  
            return null;  
  
        Object obj = beanClass.newInstance();  
  
        org.apache.commons.beanutils.BeanUtils.populate(obj, map);  
  
        return obj;  
    }    
      
    public static Map<?, ?> objectToMap(Object obj) {  
        if(obj == null)  
            return null;   
  
        return new org.apache.commons.beanutils.BeanMap(obj);  
    }    

    public static Object mapToObject1(Map<String, Object> map, Class<?> beanClass) throws Exception {    
        if (map == null)   
            return null;    
  
        Object obj = beanClass.newInstance();  
  
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());    
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();    
        for (PropertyDescriptor property : propertyDescriptors) {  
            Method setter = property.getWriteMethod();    
            if (setter != null) {  
                setter.invoke(obj, map.get(property.getName()));   
            }  
        }  
  
        return obj;  
    }    
      
    public static Map<String, Object> objectToMap1(Object obj) throws Exception {    
        if(obj == null)  
            return null;      
  
        Map<String, Object> map = new HashMap<String, Object>();   
  
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());    
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();    
        for (PropertyDescriptor property : propertyDescriptors) {    
            String key = property.getName();    
            if (key.compareToIgnoreCase("class") == 0) {   
                continue;  
            }  
            Method getter = property.getReadMethod();  
            Object value = getter!=null ? getter.invoke(obj) : null;  
            map.put(key, value);  
        }    
  
        return map;  
    }
    
    public static Object mapToObject2(Map<String, Object> map, Class<?> beanClass) throws Exception {    
        if (map == null)  
            return null;    
  
        Object obj = beanClass.newInstance();  
  
        Field[] fields = obj.getClass().getDeclaredFields();   
        for (Field field : fields) {    
            int mod = field.getModifiers();    
            if(Modifier.isStatic(mod) || Modifier.isFinal(mod)){    
                continue;    
            }    
  
            field.setAccessible(true);    
            field.set(obj, map.get(field.getName()));   
        }   
  
        return obj;    
    }    
  
    public static Map<String, Object> objectToMap2(Object obj) throws Exception {    
        if(obj == null){    
            return null;    
        }   
  
        Map<String, Object> map = new HashMap<String, Object>();    
  
        Field[] declaredFields = obj.getClass().getDeclaredFields();    
        for (Field field : declaredFields) {    
            field.setAccessible(true);  
            map.put(field.getName(), field.get(obj));  
        }    
  
        return map;  
    }
}
