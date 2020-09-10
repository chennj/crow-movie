package net.crow.moive.transfer.utls;

public final class StrUtil {
	
	public static boolean isEmpty(Object s){
		
		return (s == null || "".equals(String.valueOf(s).trim()));
	}
	
	public static boolean notEmpty(Object s){
		return !(s == null || "".equals(String.valueOf(s).trim()));
	}
}
