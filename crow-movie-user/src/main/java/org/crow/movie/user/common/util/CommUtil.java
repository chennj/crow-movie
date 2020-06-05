package org.crow.movie.user.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.RandomUtils;
import org.crow.movie.user.common.cache.FixedCache;
import org.crow.movie.user.common.constant.Const;
import org.crow.movie.user.common.db.entity.AppCdn;
import org.crow.movie.user.common.db.entity.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.alibaba.fastjson.JSONObject;

public final class CommUtil {

	private final static Logger logger = LoggerFactory.getLogger(CommUtil.class);
	
	private final static Environment environment = ApplicationUtil.getBean(Environment.class);
	
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
	
	public static <T> void updateBean(T t, JSONObject jo, ArrayList<Object> ignoreList) throws Exception{
		
		if (null == ignoreList){
			ignoreList = Const.GENERAL_FIELD_EDIT_IGNORE;
		}
		
		for (Entry<String, Object> entry : jo.entrySet()){
			
			Field f;
			Class<?> ft = null;
			Method m;
			try {
				f 	= t.getClass().getDeclaredField(entry.getKey());
				if (ignoreList.contains(f.getName())){
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
	
	/**
	 * 是否MP4
	 * @param url
	 * @return
	 */
	public static boolean isMp4(String url){
		String ext = getExt(url);
		if (StrUtil.notEmpty(ext) && (ext.equals("mp4") || ext.equals("m3u8"))){
			return true;
		}
		return false;
	}
	
	public static String getExt(String url){
		
		if (url.lastIndexOf(".") == -1){
			return null;
		}
		return url.substring(url.lastIndexOf("."));
	}
	
	public static List<AppCdn> getCdnList(){
		
		return FixedCache.appCdnCache();
	}
	
	public static String getRamdomHost(){
		
		List<AppCdn> cdnList = getCdnList();
		String host = getDomain();
		
		if (null != cdnList && !cdnList.isEmpty()){
			AppConfig appConfig = FixedCache.appConfigCache();
			Integer cdnType = appConfig.getCdnType();
			if (cdnType == 1){
				host = cdnList.get(0).getHost();
			} else {
				host = cdnList.get(RandomUtils.nextInt(0, cdnList.size())).getHost();
			}
		}
		
		return host;
	}
	
	public static String getDomain(){
		
		String protocol = "443".equals(environment.getProperty("local.server.port"))? "https://":"http://";
		InetAddress localHost = null;
		try {
			localHost = Inet4Address.getLocalHost();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(),e);
		}
		String ip = localHost.getHostAddress();  // 返回格式为：xxx.xxx.xxx
		return (protocol + ip);
	}
	
	public static String getHost(String url)
	{	
		if (StrUtil.notEmpty(url)) {
			if (url.indexOf("http") != -1) {
				return url;
			} else {
				if(isMp4(url)){
					return (getRamdomHost() + url);
				}
				String host = getDomain();
				return (host + url);
			}
		}
		return url;
	}
	
	public static String transByte(Long bytesLen)
	{
	    Long KB = 1024l;
	    Long MB = 1024 * KB;
	    Long GB = 1024 * MB;
	    Long TB = 1024 * GB;
	    if (bytesLen < KB) {
	        return String.valueOf(bytesLen)+"B";
	    } else if (bytesLen < MB) {
	        return String.valueOf(Math.round(bytesLen / KB))+"KB";
	    } else if (bytesLen < GB) {
	        return String.valueOf(Math.round(bytesLen / MB))+"MB";
	    } else if (bytesLen < TB) {
	        return String.valueOf(Math.round(bytesLen / GB))+"GB";
	    } else {
	        return String.valueOf(Math.round(bytesLen / TB))+"TB";
	    }
	}
	
	public static String getAvatar(String url)
	{
		if (StrUtil.notEmpty(url)) {
			if (url.indexOf("http") >= 0) {
				return url;
			} else {
				return getDomain() + url;
			}
		} else {
			return getDomain() + "/static/image/default_avatar.png";
		}
	}
	
	public static Integer o2i(Object object){
		return Integer.valueOf(String.valueOf(object));
	}
}
