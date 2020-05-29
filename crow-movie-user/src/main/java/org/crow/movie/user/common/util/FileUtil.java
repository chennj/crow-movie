package org.crow.movie.user.common.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public final class FileUtil {

	public static String FILE_FOLDER = "appCfg";
	
	/**
	 * 查找资源步骤<br/>
	 * 1、现在当前的classloader的根目录下找，不带appCfg，如果jar的classes下放了资源则在这里返回；如果为绝对路径则
	 * 也在这里定位资源后返回<br>
	 * 2、上面定位不到资源再在appCfg下找，下载当前的classloader的appCfg下查找，找不到再在java -jar运行目录下查找
	 * @param folder
	 * @param resource
	 * @return
	 */
	public static URL getResource(String folder, String resource){
		
		URL url = null;
		
		// 相对路径查找当前classloader的资源，绝对路径查找绝对路劲的资源
		try {
			String filename = StrUtil.isEmpty(folder)?resource:folder+File.separatorChar+resource;
			File configFile = new File(filename);
			url = configFile.toURI().toURL();
			url.openStream();
		} catch (MalformedURLException mue){
			url = null;
		} catch (IOException e){
			url = null;
		}
		
		// 先查找appCfg下的资源
		if (null == url){
			url = Thread.currentThread()
					.getContextClassLoader()
					.getResource(folder+File.separatorChar+resource);
		}
		
		//再在classPath下找
		if (null == url){
			url = Thread.currentThread()
					.getContextClassLoader()
					.getResource(resource);
		}
		
		return url;
	}
}
