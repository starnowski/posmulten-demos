package com.github.starnowski.posmulten.demos.hibernate.configurations;

import com.github.starnowski.posmulten.demos.model.User;
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
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
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
//        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.hbm2ddl.auto", "create");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        properties.put("hibernate.transaction.jta.platform", null);
        properties.put("hibernate.implicit_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
        properties.put("hibernate.id.new_generator_mappings", "false");


//        0 = {HashMap$Node@6927} "hibernate.format_sql" -> "true"
//        1 = {HashMap$Node@6928} "hibernate.transaction.jta.platform" ->
//        2 = {HashMap$Node@6929} "hibernate.hbm2ddl.auto" -> "create"
//        3 = {HashMap$Node@6930} "hibernate.id.new_generator_mappings" -> "false"
//        4 = {HashMap$Node@6931} "hibernate.physical_naming_strategy" -> "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy"
//        5 = {HashMap$Node@6932} "hibernate.dialect" -> "org.hibernate.dialect.PostgreSQLDialect"
//        6 = {HashMap$Node@6933} "hibernate.implicit_naming_strategy" -> "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy"
//        7 = {HashMap$Node@6934} "hibernate.show_sql" -> "true"
        LocalContainerEntityManagerFactoryBean bean = entityManagerFactoryBuilder
                .dataSource(datasource)
                .jta(false)
                .packages("com.github.starnowski.posmulten.demos.model")
                .persistenceUnit("pu")
                .properties(properties)
                .build();
        bean.setPackagesToScan("com.github.starnowski.posmulten.demos.model");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        bean.setJpaVendorAdapter(vendorAdapter);
        return bean;
    }
//
//    @Bean @Primary
//    public PlatformTransactionManager transactionManager(@Qualifier("emfBean") EntityManagerFactory emf) { return new JpaTransactionManager(emf); }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
