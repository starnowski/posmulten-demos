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

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfiguration {

    public static final String OWNER_TRANSACTION_MANAGER = "ownerTransactionManager";

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


    @Bean
    @ConfigurationProperties("spring.datasource.owner")
    public DataSourceProperties ownerDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "ownerDataSource")
    @ConfigurationProperties("spring.datasource.owner.configuration")
    public DataSource ownerDataSource() {
        return ownerDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "ownerJdbcTemplate")
    public JdbcTemplate ownerJdbcTemplate() {
        return new JdbcTemplate(ownerDataSource());
    }

    //

    @Bean(name = "emfBean")
    @Primary
    public LocalContainerEntityManagerFactoryBean emfBean(
            EntityManagerFactoryBuilder entityManagerFactoryBuilder,
            DataSource datasource,
            JpaProperties jpaProperties) {
        Map<String, String> properties = new HashMap<>(jpaProperties.getProperties());
        properties.put("hibernate.hbm2ddl.auto", "none");
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

    @Bean(name = "schema_emf")
    public LocalContainerEntityManagerFactoryBean emfSchemaBean(
            EntityManagerFactoryBuilder entityManagerFactoryBuilder,
            @Qualifier("ownerDataSource") DataSource ownerDataSource,
            JpaProperties jpaProperties) {
        Map<String, String> properties = new HashMap<>(jpaProperties.getProperties());
        properties.put("hibernate.hbm2ddl.auto", "create");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        return entityManagerFactoryBuilder
                .dataSource(ownerDataSource)
                .jta(false)
                .persistenceUnit("spu")
                .properties(properties)
                .build();
    }

    @Bean(name = OWNER_TRANSACTION_MANAGER)
    public PlatformTransactionManager ownerTransactionManager(
            @Qualifier("schema_emf") EntityManagerFactory emfSchemaBean) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(emfSchemaBean);
        return jpaTransactionManager;
    }
    //https://www.baeldung.com/spring-data-jpa-multiple-databases
}