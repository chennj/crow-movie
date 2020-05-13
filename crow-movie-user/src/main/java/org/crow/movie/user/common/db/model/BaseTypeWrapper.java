package org.crow.movie.user.common.db.model;

/**
 * 基本类型包装类，用于基本类型的引用传递
 * @author chenn
 *
 * @param <T>
 */
public final class BaseTypeWrapper<T> {

	T t;

	public T getT() {
		return t;
	}

	public void setT(T t) {
		this.t = t;
	}
	
	
}
