package org.crow.movie.user.common.util;

public final class Php2JavaUtil {

	public static int transTimeJ2P(long l){
		
		String ts = String.valueOf(l);
		return Integer.valueOf(ts.substring(0,ts.length()-3)).intValue();
	}
	
	public static long transTimeP2J(int i){
		
		return Long.valueOf(String.valueOf(i)+"000").longValue();
	}
	
	public static long transTimeP2J(String i){
		
		return Long.valueOf(i+"000").longValue();
	}
	
	public static int[] range(int start,int end,int step){
	    int sz =(end-start)/step;
	    int[] result=new int[sz];
	    for(int i=0;i<sz;i++)
	        result[i]=start+(i*step);
	    return result;
	}
	
	public static int ord(char ch){
		
		byte byteAscii = (byte)ch;
		return (int)byteAscii;
	}
	
	public static int ord(byte b){
		
		return (int)b;
	}
	
	public static char chr(int ascii){
		char ch = (char)ascii;
		return ch;
	}
	
	public static String substr(String src,int start, Object...objs){
		
		int lastIndex=0;
		if (objs.length>0){
			lastIndex = start+(int) objs[0];
		}
		if (lastIndex == 0){
			lastIndex = src.length();
		}
		if (start < 0){
			start = src.length()+start;
			lastIndex = src.length();
		}
		
		return src.substring(start,lastIndex);
	}
	
	public static int sumStrAscii(String str){
		byte[] bytestr = str.getBytes();
		int sum = 0;
		for(int i=0;i<bytestr.length;i++){
			sum += bytestr[i];
		}
		return sum;
	}
	
	public static String microtime(){
		
		String sec = String.valueOf(System.currentTimeMillis() / 1000);
		String non = String.valueOf(System.nanoTime()).substring(0,8);		
		return "0." + non + " " + sec;
	}
	
	public static void main(String[] args){
		
		System.out.println("c=>"+ord('c'));
		
		System.out.println("substr:"+substr("Hello world",6));
		System.out.println("substring:"+"Hello world".substring(6));
		
	}
}
