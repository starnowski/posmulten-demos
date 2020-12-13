package com.github.starnowski.posmulten.demos.util;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class TenantAwareTypeProperties {

    private String table;
    private Map<String, String> primaryKeysColumnAndTypeMap = new HashMap<>();
    private String tenantColumnName;

    public TenantAwareTypeProperties valueOf(PersistentClass persistentClass, Table table)
    {
        TenantAwareTypeProperties result = new TenantAwareTypeProperties();
        result.setTable(table.getName());
        result.setTenantColumnName("tenant_id");//TODO Resolve by adnotation or null
        return result;
    }
}
