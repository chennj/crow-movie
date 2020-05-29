package org.crow.movie.user.common;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "my.config")
public class ApplicationProperties {
	
	// referer白名单域名
    private List<String> refererDomain;

    private String uploadDir;
    
    private String qrcodeDir;
    
	public List<String> getRefererDomain() {
		return refererDomain;
	}

	public void setRefererDomain(List<String> refererDomain) {
		this.refererDomain = refererDomain;
	}

	public String getUploadDir() {
		return uploadDir;
	}

	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}

	public String getQrcodeDir() {
		return qrcodeDir;
	}

	public void setQrcodeDir(String qrcodeDir) {
		this.qrcodeDir = qrcodeDir;
	}
	
}