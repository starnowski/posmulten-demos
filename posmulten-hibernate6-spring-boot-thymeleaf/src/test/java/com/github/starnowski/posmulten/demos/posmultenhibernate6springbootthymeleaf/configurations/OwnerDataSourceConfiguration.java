package com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.configurations;

import com.github.starnowski.posmulten.hibernate.hibernate6.context.SharedSchemaContextProviderInitiator;
import com.github.starnowski.posmulten.postgresql.core.context.decorator.DefaultDecoratorContext;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
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
public class OwnerDataSourceConfiguration {

    public static final String OWNER_TRANSACTION_MANAGER = "ownerTransactionManager";
    public static final String OWNER_DATA_SOURCE = "ownerDataSource";

    @Value("${spring.datasource.primary.username}")
    private String grantee;

    @Bean(name = "ownerDataSourceProperties")
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

    @Bean(name = "schema_session_factory")
    public SessionFactory sessionFactory(@Qualifier("ownerDataSource") DataSource ownerDataSource) {

        LocalSessionFactoryBuilder builder
                = new LocalSessionFactoryBuilder(ownerDataSource);
        builder.getStandardServiceRegistryBuilder()
                .addInitiator(new SharedSchemaContextProviderInitiator(this.getClass().getResource("/configuration.yaml").getPath(), DefaultDecoratorContext.builder()
                        .withReplaceCharactersMap(Map.of("{{template_schema_value}}", "public", "{{template_user_grantee}}", grantee)).build()))
                .applySettings(hibernateProperties())
                .applySetting(Environment.DATASOURCE, ownerDataSource);
        builder.scanPackages("com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.model");
        return builder.buildSessionFactory();
    }

    private final Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty(
                "hibernate.hbm2ddl.auto", "create");
        hibernateProperties.setProperty(
                "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        hibernateProperties.setProperty(
                "hibernate.multiTenancy", "NONE");
        hibernateProperties.setProperty(
                "hibernate.archive.autodetection", "class");
        hibernateProperties.setProperty(
                "hibernate.format_sql", TRUE.toString());
        hibernateProperties.setProperty(
                "hibernate.show_sql", TRUE.toString());
        return hibernateProperties;
    }

    @Bean(name = OWNER_TRANSACTION_MANAGER)
    public PlatformTransactionManager hibernateTransactionManager(@Qualifier("schema_session_factory") SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager
                = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }
}
