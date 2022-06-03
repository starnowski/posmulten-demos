package com.github.starnowski.posmulten.demos.configurations;

import com.github.starnowski.posmulten.demos.hibernate.DefaultSharedSchemaContextBuilderFactory;
import com.github.starnowski.posmulten.postgresql.core.context.DefaultSharedSchemaContextBuilder;
import com.github.starnowski.posmulten.postgresql.core.context.ISharedSchemaContext;
import com.github.starnowski.posmulten.postgresql.core.context.exceptions.SharedSchemaContextBuilderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PosmultenConfiguration {

    @Autowired
    private DefaultSharedSchemaContextBuilderFactory defaultSharedSchemaContextBuilderFactory;

    @Bean
    public ISharedSchemaContext iSharedSchemaContext() throws SharedSchemaContextBuilderException {
        DefaultSharedSchemaContextBuilder defaultSharedSchemaContextBuilder = defaultSharedSchemaContextBuilderFactory.build();
        return defaultSharedSchemaContextBuilder.build();
    }
}
