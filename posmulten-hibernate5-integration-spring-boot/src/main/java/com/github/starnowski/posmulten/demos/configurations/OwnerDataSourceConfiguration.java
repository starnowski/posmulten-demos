package com.github.starnowski.posmulten.demos.configurations;

import com.github.starnowski.posmulten.hibernate.core.context.DefaultSharedSchemaContextBuilderMetadataEnricherProviderInitiator;
import com.github.starnowski.posmulten.hibernate.core.context.DefaultSharedSchemaContextBuilderProviderInitiator;
import com.github.starnowski.posmulten.hibernate.core.context.metadata.PosmultenUtilContextInitiator;
import com.github.starnowski.posmulten.hibernate.core.schema.PosmultenSchemaManagementTool;
import com.github.starnowski.posmulten.hibernate.core.schema.SchemaCreatorStrategyContextInitiator;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
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


//    @Bean(name = "schema_emf")
//    public LocalContainerEntityManagerFactoryBean emfSchemaBean(
//            EntityManagerFactoryBuilder entityManagerFactoryBuilder,
//            @Qualifier("ownerDataSource") DataSource ownerDataSource,
//            JpaProperties jpaProperties,
//            @Autowired PostgresRLSlHibernateSchemaManagementTool postgresRLSlHibernateSchemaManagementTool) {
//        Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
//        properties.put("hibernate.hbm2ddl.auto", "create");
//        properties.put(Environment.MULTI_TENANT, MultiTenancyStrategy.NONE);
//        properties.put(SCHEMA_MANAGEMENT_TOOL, postgresRLSlHibernateSchemaManagementTool);
//
//        LocalContainerEntityManagerFactoryBean bean = entityManagerFactoryBuilder
//                .dataSource(ownerDataSource)
//                .jta(false)
//                .persistenceUnit("spu")
//                .properties(properties)
//                .packages("com.github.starnowski.posmulten.demos.model")
//                .build();
//        bean.setPackagesToScan("com.github.starnowski.posmulten.demos.model");
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setGenerateDdl(true);
//        bean.setJpaVendorAdapter(vendorAdapter);
//        return bean;
//    }

//    @Bean(name = "schema_session_factory")
//    public LocalSessionFactoryBean sessionFactory() {
//        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
//        sessionFactory.setDataSource(ownerDataSource());
//        sessionFactory.setPackagesToScan(
//                "com.github.starnowski.posmulten.demos.model");
//        sessionFactory.setHibernateProperties(hibernateProperties());
//
//        return sessionFactory;
//    }

    @Bean(name = "schema_session_factory")
    public SessionFactory sessionFactory(@Qualifier("ownerDataSourceProperties") DataSourceProperties ownerDataSourceProperties,
                                         @Qualifier("ownerDataSource") DataSource ownerDataSource) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .addInitiator(new SchemaCreatorStrategyContextInitiator())
                .addInitiator(new DefaultSharedSchemaContextBuilderProviderInitiator())
                .addInitiator(new DefaultSharedSchemaContextBuilderMetadataEnricherProviderInitiator())
                .addInitiator(new PosmultenUtilContextInitiator())
                .applySettings(hibernateProperties(ownerDataSourceProperties))
                .applySetting(Environment.DATASOURCE, ownerDataSource)
                .build();

        SessionFactory factory = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory();
        return factory;
    }

    private final Properties hibernateProperties(DataSourceProperties ownerDataSourceProperties) {
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
//        hibernateProperties.setProperty(
//                "hibernate.connection.url", ownerDataSourceProperties.getUrl());
//        hibernateProperties.setProperty(
//                "hibernate.connection.username", ownerDataSourceProperties.getUsername());
//        hibernateProperties.setProperty(
//                "hibernate.connection.password", ownerDataSourceProperties.getPassword());
        return hibernateProperties;
    }

//    @Bean(name = OWNER_TRANSACTION_MANAGER)
//    public PlatformTransactionManager ownerTransactionManager(
//            @Qualifier("schema_emf") EntityManagerFactory emfSchemaBean) {
//        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
//        jpaTransactionManager.setEntityManagerFactory(emfSchemaBean);
//        return jpaTransactionManager;
//    }

    @Bean(name = OWNER_TRANSACTION_MANAGER)
    public PlatformTransactionManager hibernateTransactionManager(@Qualifier("schema_session_factory") SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager
                = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }
}
