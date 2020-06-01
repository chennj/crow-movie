package org.crow.movie.user.common;

import java.io.FileNotFoundException;
import java.util.UUID;

import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

public class SomeTest {

	public void objs(Object...objects){
		System.out.println(objects.length);
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		
		//Long l = 1000L;
		//System.out.println(Math.incrementExact(l));		
		//new SomeTest().objs();
		//System.out.println("当前时间："+System.currentTimeMillis());
		
		System.out.println("user.home="+System.getProperty("user.home"));
		System.out.println("user.dir="+System.getProperty("user.dir"));
		//运行结果：D:\eclipse_springboot\crow-movie\crow-movie-user		
		//String path1 = ClassUtils.getDefaultClassLoader().getResource("").getPath();
		//System.out.println(path1);
		//运行结果：/D:/eclipse_springboot/crow-movie/crow-movie-user/target/classes/
		//String path2 = ResourceUtils.getURL("classpath:").getPath();
		//System.out.println(path2);
		//运行结果：/D:/eclipse_springboot/crow-movie/crow-movie-user/target/classes/
		
		//System.out.println("==============php-java时间转换======");
		//long l = System.currentTimeMillis();
		//String ts = String.valueOf(l);
		//int i = Integer.valueOf(ts.substring(0,ts.length()-3));
		//long l1 = Long.valueOf(String.valueOf(i)+"000");
		//System.out.println("long<=>string<=>int<=>long："+l+"<=>"+ts+"<=>"+i+"<=>"+l1);	
		System.out.println("uuid:"+UUID.randomUUID().toString().replace("-", ""));
	}
}
