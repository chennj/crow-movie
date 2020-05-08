package org.crow.movie.user.common;

public class SomeTest {

	public void objs(Object...objects){
		System.out.println(objects.length);
	}
	
	public static void main(String[] args){
		
		Long l = 1000L;
		System.out.println(Math.incrementExact(l));
		
		new SomeTest().objs();
	}
}
