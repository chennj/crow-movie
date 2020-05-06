package org.crow.movie.user.common.db.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef="entityManagerFactorySecond",
        transactionManagerRef="transactionManagerSecond",
        basePackages= { "org.crow.movie.user.common.db.second.dao" }) //设置Repository所在位置
public class SecondDbConfig {

	@Autowired 
	@Qualifier("secondaryDataSource")
    private DataSource secondaryDataSource;

    @Bean(name = "entityManagerSecond")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactorySecond(builder).getObject().createEntityManager();
    }

    @Bean(name = "entityManagerFactorySecond")
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySecond (EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(secondaryDataSource)
                .properties(getVendorProperties())
                .packages("org.crow.movie.user.common.db.second.entity") //设置实体类所在位置
                .build();
    }

    @Autowired(required=false)
    private JpaProperties jpaProperties;

    private Map<String, Object> getVendorProperties() {
    	HibernateSettings hibernateSettings = new HibernateSettings();
    	return jpaProperties.getHibernateProperties(hibernateSettings);
    }

    @Bean(name = "transactionManagerSecond")
    public PlatformTransactionManager transactionManagerSecond(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactorySecond(builder).getObject());
    }
}
