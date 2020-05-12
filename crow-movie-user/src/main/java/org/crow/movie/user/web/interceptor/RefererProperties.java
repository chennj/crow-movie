package org.crow.movie.user.web.interceptor;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "my.config")
public class RefererProperties {
	
	// referer白名单域名
    private List<String> refererDomain;

	public List<String> getRefererDomain() {
		return refererDomain;
	}

	public void setRefererDomain(List<String> refererDomain) {
		this.refererDomain = refererDomain;
	}
	
    
}