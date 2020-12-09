package com.github.starnowski.posmulten.demos.hibernate.configurations;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

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

    @Bean
    @ConfigurationProperties("spring.datasource.owner.configuration")
    public DataSource ownerDataSource() {
        return ownerDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "ownerJdbcTemplate")
    public JdbcTemplate ownerJdbcTemplate() {
        return new JdbcTemplate(ownerDataSource());
    }
}