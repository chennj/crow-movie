package org.crow.movie.user.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
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
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
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

	private ApiInfo apiInfo() {
		return new ApiInfo(
				"Movie User Api",
				"视频点播客户系统API",
				"1.0-snapshot",
				"",
				new Contact("陈宁江","https://github.com/chennj/","chennj_cn@aliyun.com"),
				"MIT License"+
				""+
				"Copyright (c) 2020 chennj"+
				""+
				"Permission is hereby granted, free of charge, to any person obtaining a copy"+
				"of this software and associated documentation files (the \"Software\"), to deal"+
				"in the Software without restriction, including without limitation the rights"+
				"to use, copy, modify, merge, publish, distribute, sublicense, and/or sell"+
				"copies of the Software, and to permit persons to whom the Software is"+
				"furnished to do so, subject to the following conditions:"+
				""+
				"The above copyright notice and this permission notice shall be included in all"+
				"copies or substantial portions of the Software."+
				""+
				"THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR"+
				"IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,"+
				"FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE"+
				"AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER"+
				"LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,"+
				"OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE"+
				"SOFTWARE.\",",
				"https://github.com/chennj/crow-movie/blob/master/LICENSE");
	}
}