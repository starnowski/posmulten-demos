package com.github.starnowski.posmulten.demos.util;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@Accessors(chain = true)
@MappedSuperclass
public class TenantAware {

    @Column(name = "tenant_id", insertable = false, updatable = false)
    private String tenantId;
}
