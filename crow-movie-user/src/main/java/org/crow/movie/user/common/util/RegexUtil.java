package org.crow.movie.user.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtil {

	public static boolean isNotNumOrChar(String s){
		
		String regex = "/^[0-9a-z]*$/i";
		return isNotMatch(regex,s);
	}
	
	public static boolean isNotNum(String s){
		String regex = "/^[0-9]*$/i";
		return isNotMatch(regex,s);
	}
	
	public static boolean isNotMatch(String regex, String s){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		return !matcher.matches();
	}
}
