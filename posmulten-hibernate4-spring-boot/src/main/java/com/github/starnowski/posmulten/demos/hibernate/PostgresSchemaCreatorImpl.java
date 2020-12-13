package com.github.starnowski.posmulten.demos.hibernate;

import com.github.starnowski.posmulten.postgresql.core.common.SQLDefinition;
import com.github.starnowski.posmulten.postgresql.core.context.DefaultSharedSchemaContextBuilder;
import com.github.starnowski.posmulten.postgresql.core.context.ISharedSchemaContext;
import com.github.starnowski.posmulten.postgresql.core.context.exceptions.SharedSchemaContextBuilderException;
import org.hibernate.boot.Metadata;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.schema.internal.SchemaCreatorImpl;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toList;

@Component
public class PostgresSchemaCreatorImpl extends SchemaCreatorImpl {

    @Autowired
    private DefaultSharedSchemaContextBuilderFactory builderFactory;

    @Autowired
    private DefaultSharedSchemaContextBuilderMetadataEnricher defaultSharedSchemaContextBuilderMetadataEnricher;

    @Override
    public void doCreation(Metadata metadata, boolean createNamespaces, Dialect dialect, Target... targets) throws SchemaManagementException {
        super.doCreation(metadata, createNamespaces, dialect, targets);
        DefaultSharedSchemaContextBuilder builder = builderFactory.build();
        builder = defaultSharedSchemaContextBuilderMetadataEnricher.enrich(builder, metadata, dialect);
        try {
            ISharedSchemaContext context = builder.build();
            applySqlStrings(targets, context.getSqlDefinitions().stream().map(SQLDefinition::getCreateScript).collect(toList()).toArray(new String[0]));
        } catch (SharedSchemaContextBuilderException e) {
            throw  new SchemaManagementException("Invalid shared schema context creation", e);
        }
    }

    private static void applySqlStrings(Target[] targets, String... sqlStrings) {
        if (sqlStrings == null) {
            return;
        }

        for (Target target : targets) {
            for (String sqlString : sqlStrings) {
                target.accept(sqlString);
            }
        }
    }
}
