package com.github.starnowski.posmulten.demos.hibernate.configurations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@EnableTransactionManagement
@Configuration
public class PrimaryDataSourceConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.primary")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.primary.configuration")
    public DataSource primaryDataSource() {
        return primaryDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(primaryDataSource());
    }

    @Bean(name = "emfBean")
    @Primary
    public LocalContainerEntityManagerFactoryBean emfBean(
            EntityManagerFactoryBuilder entityManagerFactoryBuilder,
            DataSource datasource,
            JpaProperties jpaProperties) {
        Map<String, String> properties = new HashMap<>(jpaProperties.getProperties());
        properties.put("hibernate.hbm2ddl.auto", "none");//hibernate.ddl-auto
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        return entityManagerFactoryBuilder
                .dataSource(datasource)
                .jta(false)
                .persistenceUnit("pu")
                .properties(properties)
                .build();
    }

    @Bean @Primary
    public PlatformTransactionManager transactionManager(@Qualifier("emfBean") EntityManagerFactory emf) { return new JpaTransactionManager(emf); }
}
