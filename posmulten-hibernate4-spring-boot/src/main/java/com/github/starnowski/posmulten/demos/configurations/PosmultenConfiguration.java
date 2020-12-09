package com.github.starnowski.posmulten.demos.configurations;

import com.github.starnowski.posmulten.postgresql.core.context.DefaultSharedSchemaContextBuilder;
import com.github.starnowski.posmulten.postgresql.core.context.ISharedSchemaContext;
import com.github.starnowski.posmulten.postgresql.core.context.exceptions.SharedSchemaContextBuilderException;
import com.github.starnowski.posmulten.postgresql.core.rls.function.ISetCurrentTenantIdFunctionInvocationFactory;
import com.github.starnowski.posmulten.postgresql.core.rls.function.ISetCurrentTenantIdFunctionPreparedStatementInvocationFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PosmultenConfiguration {

    @Bean
    public ISharedSchemaContext iSharedSchemaContext() throws SharedSchemaContextBuilderException {
        DefaultSharedSchemaContextBuilder defaultSharedSchemaContextBuilder = new DefaultSharedSchemaContextBuilder(null); // null schema --> public schema
        defaultSharedSchemaContextBuilder.setGrantee("posmhib4sb-user"); // TODO move to configuration file
        defaultSharedSchemaContextBuilder.setCurrentTenantIdProperty("posdemo.tenant"); // TODO move to configuration file
        defaultSharedSchemaContextBuilder.setSetCurrentTenantIdFunctionName("set_pos_demo_tenant"); // TODO move to configuration file
        return defaultSharedSchemaContextBuilder.build();
    }

    @Bean
    public ISetCurrentTenantIdFunctionInvocationFactory iSetCurrentTenantIdFunctionInvocationFactory(ISharedSchemaContext iSharedSchemaContext)
    {
        return iSharedSchemaContext.getISetCurrentTenantIdFunctionInvocationFactory();
    }

    @Bean
    public ISetCurrentTenantIdFunctionPreparedStatementInvocationFactory iSetCurrentTenantIdFunctionPreparedStatementInvocationFactory(ISharedSchemaContext iSharedSchemaContext)
    {
        return iSharedSchemaContext.getISetCurrentTenantIdFunctionPreparedStatementInvocationFactory();
    }
}
