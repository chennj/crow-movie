package org.crow.movie.user.common.constant;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;

public interface Const {

	String SESSION_USER_LOGIN_KEY = "user_login_key";
	String SESSION_USER_INFO_KEY = "user_info_key";
	
	Long TS_AUTO_UPDATE_USER = (long) (60*1000);
	
	ArrayList<Object> MEMBERINFO_FIELD_EDIT_IGNORE = new ArrayList<Object>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			List<Object> list = Arrays.asList(
					new String[]{"id","account","createIp","createAddr",
							"createTime","createUser","updateTime","updateUser"});
			
			addAll(list);
		}
	};
	
	ArrayList<Object> GENERAL_FIELD_EDIT_IGNORE = new ArrayList<Object>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			List<Object> list = Arrays.asList(
					new String[]{"id","createTime","createUser"});
			
			addAll(list);
		}
	};
	
	Long TS_AUTO_FIND_APP_CDN = (long) (60 * 1000);
	Long TS_AUTO_FIND_APP_CONFIG = (long) (60 * 1000);
}
