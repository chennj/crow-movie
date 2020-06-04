package org.crow.movie.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

//@SpringBootApplication
/**
* 多数据源配置：
* 首先要在@SpringBootApplication排除该类，因为它会读取
* application.properties文件的spring.datasource.*属性并自动配置单数据源 <br>
* <p>@EnableJpaAuditing</p>
* <p>
* 作用：<br>
* 能在insert的时候，自动填充实体类中的创建人、创建时间属性，同理update的时候也能填充更新人、更新时间的属性。
* 其实这这是一个方面而已，我们也能实现记录的审计，谁新增了记录，谁删除了记录等
* </p>
* @author chenn
*
*/
@SpringBootApplication(exclude = {
      DataSourceAutoConfiguration.class
})
@ComponentScan(basePackages = {"org.crow.movie.user"})
@EnableJpaAuditing
public class App 
{
  public static void main( String[] args )
  {
  	SpringApplication.run(App.class, args);
  }
}