package com.github.starnowski.posmulten.demos.hibernate;

import com.github.starnowski.posmulten.postgresql.core.common.SQLDefinition;
import com.github.starnowski.posmulten.postgresql.core.context.DefaultSharedSchemaContextBuilder;
import com.github.starnowski.posmulten.postgresql.core.context.ISharedSchemaContext;
import com.github.starnowski.posmulten.postgresql.core.context.exceptions.SharedSchemaContextBuilderException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Exportable;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.internal.SchemaCreatorImpl;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.hibernate.tool.schema.spi.Target;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Component
public class PostgresSchemaCreatorImpl extends SchemaCreatorImpl {

    @Autowired
    private DefaultSharedSchemaContextBuilderFactory builderFactory;

    @Override
    public void doCreation(Metadata metadata, boolean createNamespaces, Dialect dialect, Target... targets) throws SchemaManagementException {
        super.doCreation(metadata, createNamespaces, dialect, targets);
        DefaultSharedSchemaContextBuilder builder = builderFactory.build();
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


            // Add priviledge to policyTargetUsern for sequences
            for (Sequence sequence : namespace.getSequences()) {
                checkExportIdentifier(sequence, exportIdentifiers);
//                applySqlStrings(
//                        targets,
//                        new PolicyTargetUserTableSequenceSqlGenerator(databaseProperties.getUser()).getSqlCreateStrings(
//                                jdbcEnvironment.getQualifiedObjectNameFormatter().format(sequence.getName(), dialect)
//                        )
//                );
            }

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

        try {
            ISharedSchemaContext context = builder.build();
            applySqlStrings(targets, context.getSqlDefinitions().stream().map(SQLDefinition::getCreateScript).collect(toList()).toArray(new String[0]));
        } catch (SharedSchemaContextBuilderException e) {
            throw  new SchemaManagementException("Invalid shared schema context creation", e);
        }
    }

    private static void checkExportIdentifier(Exportable exportable, Set<String> exportIdentifiers) {
        final String exportIdentifier = exportable.getExportIdentifier();
        if (exportIdentifiers.contains(exportIdentifier)) {
            throw new SchemaManagementException("SQL strings added more than once for: " + exportIdentifier);
        }
        exportIdentifiers.add(exportIdentifier);
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
