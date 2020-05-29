package org.crow.movie.user.common.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class PropertyUtil {

	private static PropertyUtil instance_ = null;
	
	private static Map<String, Properties> propMap = new HashMap<>();
	
	private PropertyUtil(){}
	
	public synchronized static PropertyUtil instance(){
		if (null == instance_){
			instance_ = new PropertyUtil();
		}
		return instance_;
	}
	
	public String getValueByDefaultFileKey(String fileName, String key) throws IOException{
		return (String)getPropertiesByFileName(null, fileName).getProperty(key);
	}
	
	public String getValueByDefaultFileKey(String dir, String fileName, String key) throws IOException{
		return (String)getPropertiesByFileName(dir, fileName).getProperty(key);
	}
	
	public Properties getPropertiesByFileName(String dir, String fileName) throws IOException{
		
		String fileName_;
		if (StrUtil.notEmpty(dir)){
			fileName_ = dir + File.separator + fileName;
		} else {
			fileName_ = fileName;
		}
		
		Properties prop = propMap.get(fileName_);
		if (null != prop){
			return prop;
		} else {
			URL url = StrUtil.notEmpty(dir) ?
					FileUtil.getResource(null, fileName_) :
					FileUtil.getResource(FileUtil.FILE_FOLDER, fileName_) ;
			if (null == url){
				throw new IOException("找不到属性文件："+fileName_);
			}
			
			Properties p = new Properties();
			try {
				System.out.println("属性文件被找到：URL="+url.toExternalForm());
				p.load(url.openStream());
			} catch (IOException e){
				System.out.println("加载属性文件失败：URL="+url.toExternalForm());
				throw new IOException("加载属性文件失败：URL="+url.toExternalForm(),e);
			}
			propMap.put(fileName_, p);
			return p;
		}
	}
}
