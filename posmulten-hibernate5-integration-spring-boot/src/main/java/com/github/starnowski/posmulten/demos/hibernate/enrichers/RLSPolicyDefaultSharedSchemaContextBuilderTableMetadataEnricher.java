package com.github.starnowski.posmulten.demos.hibernate.enrichers;

import com.github.starnowski.posmulten.demos.hibernate.IDefaultSharedSchemaContextBuilderTableMetadataEnricher;
import com.github.starnowski.posmulten.demos.util.NameGenerator;
import com.github.starnowski.posmulten.demos.util.TenantAware;
import com.github.starnowski.posmulten.demos.util.TenantAwareTypeProperties;
import com.github.starnowski.posmulten.postgresql.core.context.DefaultSharedSchemaContextBuilder;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RLSPolicyDefaultSharedSchemaContextBuilderTableMetadataEnricher implements IDefaultSharedSchemaContextBuilderTableMetadataEnricher {
    @Override
    public DefaultSharedSchemaContextBuilder enrich(DefaultSharedSchemaContextBuilder builder, Metadata metadata, Table table) {
        if (!table.isPhysicalTable()) {
            return builder;
        }
        Optional<PersistentClass> pClass = metadata.getEntityBindings().stream().filter(persistentClass -> table.equals(persistentClass.getTable())).findFirst();
        if (!pClass.isPresent()) {
            return builder;
        }
        PersistentClass persistentClass = pClass.get();
        if (TenantAware.class.isAssignableFrom(persistentClass.getMappedClass())) {
            TenantAwareTypeProperties properties = TenantAwareTypeProperties.valueOf(persistentClass, table);
            String policyName = NameGenerator.generate(64, "rls_policy_", properties.getTable(), table.getSchema());
            builder.createRLSPolicyForTable(properties.getTable(), properties.getPrimaryKeysColumnAndTypeMap(), properties.getTenantColumnName(), policyName);
        }
        return builder;
    }
}
