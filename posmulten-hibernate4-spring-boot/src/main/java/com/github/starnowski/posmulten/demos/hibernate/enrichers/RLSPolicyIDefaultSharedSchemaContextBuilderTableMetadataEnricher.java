package com.github.starnowski.posmulten.demos.hibernate.enrichers;

import com.github.starnowski.posmulten.demos.hibernate.IDefaultSharedSchemaContextBuilderTableMetadataEnricher;
import com.github.starnowski.posmulten.demos.util.TenantAware;
import com.github.starnowski.posmulten.postgresql.core.context.DefaultSharedSchemaContextBuilder;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;

import java.util.Optional;

public class RLSPolicyIDefaultSharedSchemaContextBuilderTableMetadataEnricher implements IDefaultSharedSchemaContextBuilderTableMetadataEnricher {
    @Override
    public DefaultSharedSchemaContextBuilder enrich(DefaultSharedSchemaContextBuilder builder, Metadata metadata, Table table) {
        Optional<PersistentClass> pClass = metadata.getEntityBindings().stream().filter(persistentClass -> table.equals(persistentClass.getTable())).findFirst();
        if (!pClass.isPresent()) {
            return builder;
        }
        PersistentClass persistentClass = pClass.get();
        if (TenantAware.class.isAssignableFrom(persistentClass.getMappedClass())) {
//            return new String[]{createPolicyStatement(table.getName())};
        }
        return builder;
    }
}
