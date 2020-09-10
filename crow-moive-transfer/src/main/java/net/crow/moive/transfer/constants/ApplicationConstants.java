package net.crow.moive.transfer.constants;

public interface ApplicationConstants {

    String CONTENT_TYPE = "Content-Type";
    String CONTENT_LENGTH = "Content-Length";
    String VIDEO_CONTENT = "video/";
    String CONTENT_RANGE = "Content-Range";
    String ACCEPT_RANGES = "Accept-Ranges";
    String BYTES = "bytes";
    int BYTE_RANGE = 1024;
    
    /*****************配置文件************************/
	String CONFIG_COMMON_FILE = "appCfg/video-server-common.properties";
	String VIDEO_PATH_CONFIG = "video.share.path";
}
