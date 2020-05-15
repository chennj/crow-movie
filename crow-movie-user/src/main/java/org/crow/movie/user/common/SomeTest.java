package org.crow.movie.user.common;

import java.io.FileNotFoundException;

import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

public class SomeTest {

	public void objs(Object...objects){
		System.out.println(objects.length);
	}
	
	public static void main(String[] args) throws FileNotFoundException{
		
//		Long l = 1000L;
//		System.out.println(Math.incrementExact(l));
//		
//		new SomeTest().objs();
		
		System.out.println(System.getProperty("user.dir"));
		//运行结果：D:\eclipse_springboot\crow-movie\crow-movie-user		
		String path1 = ClassUtils.getDefaultClassLoader().getResource("").getPath();
		System.out.println(path1);
		//运行结果：/D:/eclipse_springboot/crow-movie/crow-movie-user/target/classes/
		String path2 = ResourceUtils.getURL("classpath:").getPath();
		System.out.println(path2);
		//运行结果：/D:/eclipse_springboot/crow-movie/crow-movie-user/target/classes/
	}
}
