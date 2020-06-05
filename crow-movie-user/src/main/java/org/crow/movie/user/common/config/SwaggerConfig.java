package org.crow.movie.user.common.config;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer{
	
	@Bean
	public Docket api(){
		return new Docket(DocumentationType.SWAGGER_2)
				.enable(true)
				.apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.crow.movie.user.web.api"))
                //.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
	}
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
 
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

	@SuppressWarnings("rawtypes")
	private ApiInfo apiInfo() {
		return new ApiInfo(
				"Movie User Api",
				"视频点播客户系统API,注：所有时间格式用yyyy-MM-dd HH:mm:ss,eg:2020-06-05 00:00:00",
				"1.0-snapshot",
				"",
				new Contact("老陈","https://github.com/chennj/","chennj_cn@aliyun.com"),
				"",
				"https://github.com/chennj/crow-movie/blob/master/LICENSE",
				 new ArrayList<VendorExtension>());
	}
}