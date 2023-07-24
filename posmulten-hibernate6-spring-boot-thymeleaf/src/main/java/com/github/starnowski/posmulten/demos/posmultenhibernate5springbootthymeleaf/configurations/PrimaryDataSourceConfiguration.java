package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.configurations;

import com.github.starnowski.posmulten.hibernate.common.context.CurrentTenantContext;
import com.github.starnowski.posmulten.hibernate.hibernate6.connection.SharedSchemaConnectionProviderInitiatorAdapter;
import com.github.starnowski.posmulten.hibernate.hibernate6.context.SharedSchemaContextProviderInitiator;
import com.github.starnowski.posmulten.postgresql.core.context.decorator.DefaultDecoratorContext;
import org.apache.groovy.util.Maps;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

import static java.lang.Boolean.TRUE;

@EnableTransactionManagement
@Configuration
@EnableJpaRepositories(basePackages = "com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.repositories", transactionManagerRef = "hibernateTransactionManager", entityManagerFactoryRef = "primary_session_factory")
public class PrimaryDataSourceConfiguration {

    public static final String SET_CURRENT_TENANT_FUNCTION_NAME = "set_pos_demo_tenant";

    @Value("${spring.datasource.primary.username}")
    private String grantee;

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


    @Bean(name = "primary_session_factory")
    @Primary
    public SessionFactory sessionFactory(DataSource primaryDataSource) {
        CurrentTenantContext.setCurrentTenant("XXXXX");

        LocalSessionFactoryBuilder builder
                = new LocalSessionFactoryBuilder(primaryDataSource);
        final StandardServiceRegistry registry = builder.getStandardServiceRegistryBuilder()
                .addInitiator(new SharedSchemaConnectionProviderInitiatorAdapter())
                .addInitiator(new SharedSchemaContextProviderInitiator(this.getClass().getResource("/configuration.yaml").getPath(), DefaultDecoratorContext.builder()
                        .withReplaceCharactersMap(Map.of("{{template_schema_value}}", "public", "{{template_user_grantee}}", grantee)).build()))

                .applySettings(hibernateProperties())
                .applySetting(Environment.DATASOURCE, primaryDataSource)
                .build();
        builder.scanPackages("com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.model");
        return builder.buildSessionFactory();
    }

    private final Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty(
                "hibernate.hbm2ddl.auto", "none");
        hibernateProperties.setProperty(
                "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        hibernateProperties.setProperty(
                "hibernate.multiTenancy", "SCHEMA");
        hibernateProperties.setProperty(
                Environment.MULTI_TENANT_CONNECTION_PROVIDER, "com.github.starnowski.posmulten.hibernate.core.connections.SharedSchemaMultiTenantConnectionProvider");
        hibernateProperties.setProperty(
                Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, "com.github.starnowski.posmulten.hibernate.core.CurrentTenantIdentifierResolverImpl");
        hibernateProperties.setProperty(
                "hibernate.archive.autodetection", "class");
        hibernateProperties.setProperty(
                "hibernate.format_sql", TRUE.toString());
        hibernateProperties.setProperty(
                "hibernate.show_sql", TRUE.toString());
        hibernateProperties.setProperty(
                Environment.PERSISTENCE_UNIT_NAME, "pu");
        return hibernateProperties;
    }


    @Bean(name = "hibernateTransactionManager")
    @Primary
    public PlatformTransactionManager hibernateTransactionManager(@Qualifier("primary_session_factory") SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager
                = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }
}
