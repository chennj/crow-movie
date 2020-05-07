package org.crow.movie.user.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map.Entry;

import org.crow.movie.user.common.constant.Const;

import com.alibaba.fastjson.JSONObject;

public final class SomeUtil {

	/**
	 * @description long to int
	 * @param l
	 * @return
	 * 
	 */
	public static int safeLongToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        return 0;
	    }
	    return (int) l;
	}
	
	/**
	 * @description 将传入字符串的首字母大写
	 * @param str 传入的字符串
	 * @return
	 */
	public static String initCap(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z')
            ch[0] = (char) (ch[0] - 32);
        return new String(ch);
    }
	
	public static <T> void updateBean(T t, JSONObject jo) throws Exception{
		
		for (Entry<String, Object> entry : jo.entrySet()){
			
			Field f;
			Class<?> ft = null;
			Method m;
			try {
				f 	= t.getClass().getDeclaredField(entry.getKey());
				if (Const.MEMBERINFO_FIELD_EDIT_IGNORE.contains(f.getName())){
					continue;
				}
				ft 	= f.getType();
				m 	= t.getClass().getDeclaredMethod("set"+initCap(entry.getKey()), new Class[]{ft});
				m.invoke(t, new Object[]{getClassTypeValue(ft, entry.getValue())});				
			} catch (NoSuchFieldException e){
				 throw new Exception (t.getClass().getSimpleName()+" have not field "+entry.getKey());
			} catch (NoSuchMethodException e) {
				throw new Exception (t.getClass().getSimpleName()+" have not method set"+initCap(entry.getKey())+"("+ft.getName()+")");
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
				throw new Exception (e.getMessage());
			}
		}
	}
	
	private static Object getClassTypeValue(Class<?> typeClass, Object value){
        if(typeClass == int.class  || value instanceof Integer){
            if(null == value){
                return 0;
            }
            return value;
        }else if(typeClass == short.class){
            if(null == value){
                return 0;
            }
            return value;
        }else if(typeClass == byte.class){
            if(null == value){
                return 0;
            }
            return value;
        }else if(typeClass == double.class){
            if(null == value){
                return 0;
            }
            return value;
        }else if(typeClass == long.class){
            if(null == value){
                return 0;
            }
            return value;
        }else if(typeClass == String.class){
            if(null == value){
                return "";
            }
            return value;
        }else if(typeClass == boolean.class){
            if(null == value){
                return true;
            }
            return value;
        }else if(typeClass == BigDecimal.class){
            if(null == value){
                return new BigDecimal(0);
            }
            return new BigDecimal(value+"");
        }else {
            return typeClass.cast(value);
        }
	}
}
