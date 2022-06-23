package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.configurations;

import com.github.starnowski.posmulten.hibernate.core.connections.CurrentTenantPreparedStatementSetterInitiator;
import com.github.starnowski.posmulten.hibernate.core.connections.SharedSchemaConnectionProviderInitiatorAdapter;
import com.github.starnowski.posmulten.hibernate.core.context.CurrentTenantContext;
import com.github.starnowski.posmulten.hibernate.core.context.DefaultSharedSchemaContextBuilderProviderInitiator;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.Properties;

import static com.github.starnowski.posmulten.hibernate.core.Properties.SET_CURRENT_TENANT_FUNCTION_NAME;
import static java.lang.Boolean.TRUE;

@EnableTransactionManagement
@Configuration
@EnableJpaRepositories(basePackages = "com.github.starnowski.posmulten.demos.dao", transactionManagerRef = "hibernateTransactionManager", entityManagerFactoryRef = "primary_session_factory")
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


    @Bean(name = "primary_session_factory")
    @Primary
    public SessionFactory sessionFactory(DataSource primaryDataSource) {

        LocalSessionFactoryBuilder builder
                = new LocalSessionFactoryBuilder(primaryDataSource);
        builder.getStandardServiceRegistryBuilder()
                .addInitiator(new SharedSchemaConnectionProviderInitiatorAdapter())
                .addInitiator(new DefaultSharedSchemaContextBuilderProviderInitiator())
                .addInitiator(new CurrentTenantPreparedStatementSetterInitiator())
                .applySettings(hibernateProperties())
                // https://stackoverflow.com/questions/39064124/unknownunwraptypeexception-cannot-unwrap-to-requested-type-javax-sql-datasourc
                .applySetting(Environment.DATASOURCE, primaryDataSource);
        builder.scanPackages("com.github.starnowski.posmulten.demos.model");
        //TODO Fix
        CurrentTenantContext.setCurrentTenant("XXXXX");
        return builder.buildSessionFactory();
    }

    private final Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty(
                "hibernate.hbm2ddl.auto", "none");
        hibernateProperties.setProperty(
                "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        hibernateProperties.setProperty(
                Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA.name());
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
                "hibernate.posmulten.schema.builder.provider", "lightweight");
        hibernateProperties.setProperty(
                Environment.PERSISTENCE_UNIT_NAME, "pu");
        hibernateProperties.setProperty(
                SET_CURRENT_TENANT_FUNCTION_NAME, SET_CURRENT_TENANT_FUNCTION_NAME);
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
