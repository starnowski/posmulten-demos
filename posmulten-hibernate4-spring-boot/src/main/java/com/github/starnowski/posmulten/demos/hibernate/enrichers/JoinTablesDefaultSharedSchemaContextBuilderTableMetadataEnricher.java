package com.github.starnowski.posmulten.demos.hibernate.enrichers;

import com.github.starnowski.posmulten.demos.hibernate.IDefaultSharedSchemaContextBuilderTableMetadataEnricher;
import com.github.starnowski.posmulten.demos.util.NameGenerator;
import com.github.starnowski.posmulten.postgresql.core.context.DefaultSharedSchemaContextBuilder;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;

@Component
public class JoinTablesDefaultSharedSchemaContextBuilderTableMetadataEnricher implements IDefaultSharedSchemaContextBuilderTableMetadataEnricher {
    @Override
    public DefaultSharedSchemaContextBuilder enrich(DefaultSharedSchemaContextBuilder builder, Metadata metadata, Table table) {
        Optional<Collection> pCollection = metadata.getCollectionBindings().stream().filter(collection -> table.equals(collection.getCollectionTable())).findFirst();
        Optional<PersistentClass> pClass = metadata.getEntityBindings().stream().filter(persistentClass -> table.equals(persistentClass.getTable())).findFirst();
        if (!pCollection.isPresent() || pClass.isPresent()) {
            return builder;
        }
        builder.createTenantColumnForTable(table.getName());
        String policyName = NameGenerator.generate(64, "rls_policy_", table.getName(), table.getSchema());
        builder.createRLSPolicyForTable(table.getName(), new HashMap<>(), "tenant_id", policyName);
        return builder;
    }
}
