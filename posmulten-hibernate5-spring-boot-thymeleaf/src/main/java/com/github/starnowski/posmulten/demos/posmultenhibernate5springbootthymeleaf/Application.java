package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * Because of issue :
 * Caused by: org.springframework.web.util.NestedServletException: Request processing failed; nested exception is java.lang.ClassCastException: org.springframework.orm.jpa.EntityManagerHolder cannot be cast to org.springframework.orm.hibernate5.SessionHolder
 * https://stackoverflow.com/questions/42476261/classcastexception-org-springframework-orm-jpa-entitymanagerholder-cannot-be-ca
 * there is a need for exclusion of configration  HibernateJpaAutoConfiguration
 *
 */
@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

}