package com.github.starnowski.posmulten.demos.hibernate;

import org.hibernate.boot.Metadata;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.schema.internal.SchemaCreatorImpl;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.Target;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostgresSchemaCreatorImpl extends SchemaCreatorImpl {

    @Override
    public void doCreation(Metadata metadata, boolean createNamespaces, Dialect dialect, List<Target> targets) throws SchemaManagementException {
        super.doCreation(metadata, createNamespaces, dialect, targets);
    }
}
