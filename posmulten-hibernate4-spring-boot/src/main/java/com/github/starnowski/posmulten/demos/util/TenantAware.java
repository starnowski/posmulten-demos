package com.github.starnowski.posmulten.demos.util;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;

@Data
@Accessors(chain = true)
public class TenantAware {

    @Column(name = "tenant_id")
    private String tenantId;
}
