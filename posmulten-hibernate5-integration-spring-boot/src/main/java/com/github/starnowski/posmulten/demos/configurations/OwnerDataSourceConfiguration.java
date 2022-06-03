package com.github.starnowski.posmulten.demos.configurations;

import com.github.starnowski.posmulten.hibernate.core.context.DefaultSharedSchemaContextBuilderMetadataEnricherProviderInitiator;
import com.github.starnowski.posmulten.hibernate.core.context.DefaultSharedSchemaContextBuilderProviderInitiator;
import com.github.starnowski.posmulten.hibernate.core.context.metadata.PosmultenUtilContextInitiator;
import com.github.starnowski.posmulten.hibernate.core.schema.PosmultenSchemaManagementTool;
import com.github.starnowski.posmulten.hibernate.core.schema.SchemaCreatorStrategyContextInitiator;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.Properties;

import static java.lang.Boolean.TRUE;

@EnableTransactionManagement
@Configuration
public class OwnerDataSourceConfiguration {

    public static final String OWNER_TRANSACTION_MANAGER = "ownerTransactionManager";
    public static final String OWNER_DATA_SOURCE = "ownerDataSource";
    public static final String SET_CURRENT_TENANT_FUNCTION_NAME = "set_pos_demo_tenant";
    public static final String TENANT_PROPERTY_NAME = "posdemo.tenant";

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
                .addInitiator(new SchemaCreatorStrategyContextInitiator())
                .addInitiator(new DefaultSharedSchemaContextBuilderProviderInitiator())
                .addInitiator(new DefaultSharedSchemaContextBuilderMetadataEnricherProviderInitiator())
                .addInitiator(new PosmultenUtilContextInitiator())
                .applySettings(hibernateProperties())
                // https://stackoverflow.com/questions/39064124/unknownunwraptypeexception-cannot-unwrap-to-requested-type-javax-sql-datasourc
                .applySetting(Environment.DATASOURCE, ownerDataSource);
        builder.scanPackages("com.github.starnowski.posmulten.demos.model");
        return builder.buildSessionFactory();
    }

    private final Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty(
                "hibernate.hbm2ddl.auto", "create");
        hibernateProperties.setProperty(
                "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        hibernateProperties.setProperty(
                Environment.MULTI_TENANT, MultiTenancyStrategy.NONE.name());
        hibernateProperties.setProperty(
                "hibernate.archive.autodetection", "class");
        hibernateProperties.setProperty(
                "hibernate.schema_management_tool", PosmultenSchemaManagementTool.class.getName());
        hibernateProperties.setProperty(
                "hibernate.format_sql", TRUE.toString());
        hibernateProperties.setProperty(
                "hibernate.show_sql", TRUE.toString());
        hibernateProperties.setProperty(
                "hibernate.posmulten.grantee", "posmhib4sb-user");
        hibernateProperties.setProperty(
                com.github.starnowski.posmulten.hibernate.core.Properties.SET_CURRENT_TENANT_FUNCTION_NAME, SET_CURRENT_TENANT_FUNCTION_NAME);
        hibernateProperties.setProperty(
                com.github.starnowski.posmulten.hibernate.core.Properties.ID_PROPERTY, TENANT_PROPERTY_NAME);
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
