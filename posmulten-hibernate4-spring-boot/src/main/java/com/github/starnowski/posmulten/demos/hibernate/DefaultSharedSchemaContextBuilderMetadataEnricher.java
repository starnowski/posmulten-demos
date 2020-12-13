package com.github.starnowski.posmulten.demos.hibernate;

import com.github.starnowski.posmulten.postgresql.core.context.DefaultSharedSchemaContextBuilder;
import org.hibernate.boot.Metadata;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.schema.spi.Target;

public class DefaultSharedSchemaContextBuilderMetadataEnricher {

    public DefaultSharedSchemaContextBuilder enrich(DefaultSharedSchemaContextBuilder builder, Metadata metadata, Dialect dialect, Target... targets)
    {
        //TODO
        return builder;
    }
}
