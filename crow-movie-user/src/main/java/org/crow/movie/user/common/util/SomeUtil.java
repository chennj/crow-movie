package org.crow.movie.user.common.util;

public class SomeUtil {

	public static int safeLongToInt(long l) {
	    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
	        return 0;
	    }
	    return (int) l;
	}
}
