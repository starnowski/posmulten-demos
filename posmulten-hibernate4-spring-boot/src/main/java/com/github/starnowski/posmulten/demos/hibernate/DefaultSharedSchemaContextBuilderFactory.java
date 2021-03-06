package com.github.starnowski.posmulten.demos.hibernate;

import com.github.starnowski.posmulten.postgresql.core.context.DefaultSharedSchemaContextBuilder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.github.starnowski.posmulten.demos.util.TenantContext.INVALID_TENANT_ID;

@Component
public class DefaultSharedSchemaContextBuilderFactory {

    public DefaultSharedSchemaContextBuilder build()
    {
        DefaultSharedSchemaContextBuilder defaultSharedSchemaContextBuilder = new DefaultSharedSchemaContextBuilder(null); // null schema --> public schema
        defaultSharedSchemaContextBuilder.setGrantee("posmhib4sb-user"); // TODO move to configuration file
        defaultSharedSchemaContextBuilder.setCurrentTenantIdProperty("posdemo.tenant"); // TODO move to configuration file
        defaultSharedSchemaContextBuilder.setSetCurrentTenantIdFunctionName("set_pos_demo_tenant"); // TODO move to configuration file
        defaultSharedSchemaContextBuilder.setCurrentTenantIdentifierAsDefaultValueForTenantColumnInAllTables(true); // TODO move to configuration file
        defaultSharedSchemaContextBuilder.createValidTenantValueConstraint(Arrays.asList(INVALID_TENANT_ID, "tenants"), null, null); // TODO move to configuration file
        return defaultSharedSchemaContextBuilder;
    }
}
