package org.crow.movie.user.common.util;

public final class StrUtil {

	public static boolean isEmpty(String s){
		
		return s == null || s.trim().length() == 0;
	}
	
public static boolean isEmpty(Integer i){
		
		return i == null || i == 0;
	}
}
