package com.github.starnowski.posmulten.demos.hibernate.configurations;

import com.github.starnowski.posmulten.demos.hibernate.PostgresRLSlHibernateSchemaManagementTool;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static com.github.starnowski.posmulten.demos.hibernate.configurations.DataSourceConfiguration.OWNER_TRANSACTION_MANAGER;
import static org.hibernate.cfg.AvailableSettings.SCHEMA_MANAGEMENT_TOOL;

@EnableTransactionManagement
@Configuration
public class OwnerDataSourceConfiguration {

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


    @Bean(name = "schema_emf")
    public LocalContainerEntityManagerFactoryBean emfSchemaBean(
            EntityManagerFactoryBuilder entityManagerFactoryBuilder,
            @Qualifier("ownerDataSource") DataSource ownerDataSource,
            JpaProperties jpaProperties,
            @Autowired PostgresRLSlHibernateSchemaManagementTool postgresRLSlHibernateSchemaManagementTool) {
        Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
        properties.put("hibernate.hbm2ddl.auto", "create");
        properties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.NONE);
        properties.put(SCHEMA_MANAGEMENT_TOOL, postgresRLSlHibernateSchemaManagementTool);

        LocalContainerEntityManagerFactoryBean bean = entityManagerFactoryBuilder
                .dataSource(ownerDataSource)
                .jta(false)
                .persistenceUnit("spu")
                .properties(properties)
                .packages("com.github.starnowski.posmulten.demos.model")
                .build();
        bean.setPackagesToScan("com.github.starnowski.posmulten.demos.model");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        bean.setJpaVendorAdapter(vendorAdapter);
        return bean;
    }

    @Bean(name = OWNER_TRANSACTION_MANAGER)
    public PlatformTransactionManager ownerTransactionManager(
            @Qualifier("schema_emf") EntityManagerFactory emfSchemaBean) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(emfSchemaBean);
        return jpaTransactionManager;
    }
}
