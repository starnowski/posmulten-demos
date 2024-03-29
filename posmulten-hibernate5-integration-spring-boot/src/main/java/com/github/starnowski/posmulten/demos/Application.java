package com.github.starnowski.posmulten.demos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * Excluded HibernateJpaAutoConfiguration configuration because https://stackoverflow.com/questions/42476261/classcastexception-org-springframework-orm-jpa-entitymanagerholder-cannot-be-ca
 */
@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class Application {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
