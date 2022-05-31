package com.github.starnowski.posmulten.demos.hibernate.enrichers;

import com.github.starnowski.posmulten.demos.hibernate.IDefaultSharedSchemaContextBuilderTableMetadataEnricher;
import com.github.starnowski.posmulten.demos.util.NameGenerator;
import com.github.starnowski.posmulten.demos.util.TenantAware;
import com.github.starnowski.posmulten.postgresql.core.context.DefaultSharedSchemaContextBuilder;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class ForeignKeySharedSchemaContextBuilderTableMetadataEnricher implements IDefaultSharedSchemaContextBuilderTableMetadataEnricher {
    @Override
    public DefaultSharedSchemaContextBuilder enrich(DefaultSharedSchemaContextBuilder builder, Metadata metadata, Table table) {
        // foreign keys
        final Iterator fkItr = table.getForeignKeyIterator();
        while (fkItr.hasNext()) {
            final ForeignKey foreignKey = (ForeignKey) fkItr.next();
            try {
                //TODO Fix resolving entity class
                if (foreignKey.getReferencedEntityName() != null && TenantAware.class.isAssignableFrom(Class.forName(foreignKey.getReferencedEntityName()))) {
                    Table referenceTable = foreignKey.getReferencedTable();
                    enrichBuilder(builder, foreignKey);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return builder;
    }

    private void enrichBuilder(DefaultSharedSchemaContextBuilder builder, ForeignKey foreignKey) {
        String functionName = NameGenerator.generate(64, "rls_fk_con_", foreignKey.getTable().getName(), foreignKey.getTable().getSchema(), foreignKey.getReferencedTable().getName(), foreignKey.getReferencedTable().getSchema());
        List<Column> referenceColumns = foreignKey.getReferencedTable().getPrimaryKey().getColumns();
        List<Column> columns = foreignKey.getColumns();
        Map<String, String> foreignKeyToPrimaryKeyMap = new HashMap<>();
        for (int i = 0; i < columns.size(); i++)
        {
            foreignKeyToPrimaryKeyMap.put(columns.get(i).getName(), referenceColumns.get(i).getName());
        }
        builder.createSameTenantConstraintForForeignKey(foreignKey.getTable().getName(), foreignKey.getReferencedTable().getName(), foreignKeyToPrimaryKeyMap, functionName);
    }
}
