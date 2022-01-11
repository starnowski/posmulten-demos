package com.github.starnowski.posmulten.demos.hibernate;

import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.spi.SchemaCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PostgresRLSlHibernateSchemaManagementTool extends HibernateSchemaManagementTool {

    @Autowired
    private PostgresSchemaCreatorImpl postgresSchemaCreator;

    @Override
    public SchemaCreator getSchemaCreator(Map options) {
        return postgresSchemaCreator;
    }

}
