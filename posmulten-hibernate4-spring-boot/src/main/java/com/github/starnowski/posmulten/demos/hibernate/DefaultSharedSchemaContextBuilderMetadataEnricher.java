package com.github.starnowski.posmulten.demos.hibernate;

import com.github.starnowski.posmulten.postgresql.core.context.DefaultSharedSchemaContextBuilder;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DefaultSharedSchemaContextBuilderMetadataEnricher {

    @Autowired
    private List<IDefaultSharedSchemaContextBuilderTableMetadataEnricher> enrichers;

    public DefaultSharedSchemaContextBuilder enrich(DefaultSharedSchemaContextBuilder builder, Metadata metadata, Dialect dialect)
    {
        final Database database = metadata.getDatabase();
        final Set<String> exportIdentifiers = new HashSet<>(50);
        final JdbcEnvironment jdbcEnvironment = database.getJdbcEnvironment();
        //TODO Add tests
        //TODO Create

        // then, create all schema objects (tables, sequences, constraints, etc) in each schema
        for (Namespace namespace : database.getNamespaces()) {


            // Create RLS policy for entities which extends TenantSupport
            for (Table table : namespace.getTables()) {
                if (!table.isPhysicalTable()) {
                    continue;
                }
//                applySqlStrings(
//                        targets,
//                        new PostgresRlsPolicyForEntityExporter(databaseProperties.getUser()).getSqlCreateStrings(table, metadata)
//                );
//                applySqlStrings(
//                        targets,
//                        new PostgresRlsPolicyForJoinTablesExporter(databaseProperties.getUser()).getSqlCreateStrings(table, metadata)
//                );
//                applySqlStrings(
//                        targets,
//                        new PolicyTargetUserTablePrivilegesExporter(databaseProperties.getUser()).getSqlCreateStrings(table, metadata)
//                );
//                applySqlStrings(
//                        targets,
//                        new TenantIdColumnDefinitionExporter().getSqlCreateStrings(table, metadata)
//                );
            }

            //TODO Add foreignKey constraint
            //TODO Ask Szymon - did I ever seen FK by Hibernate?
            //ForeignKeyCorrectTenantCheckExporter
            Map<String, String> tableToCheckFunctionName = new HashMap<>();
            for (Table table : namespace.getTables()) {
                // foreign keys
                final Iterator fkItr = table.getForeignKeyIterator();
                while (fkItr.hasNext()) {
                    final ForeignKey foreignKey = (ForeignKey) fkItr.next();
//                    ForeignKeyCorrectTenantCheckExporter foreignKeyCorrectTenantCheckExporter = new ForeignKeyCorrectTenantCheckExporter();
//                    ForeignKeyCorrectTenantCheckExporter.ForeignKeyCorrectTenantCheckExporterResult fkResult = foreignKeyCorrectTenantCheckExporter.export(foreignKey, tableToCheckFunctionName);
//
//                    applySqlStrings(
//                            targets,
//                            fkResult.getResult().toArray(new String[0])
//                    );
//                    tableToCheckFunctionName = fkResult.getTableToCheckFunctionName();
                }
            }
        }
        //TODO
        return builder;
    }
}
