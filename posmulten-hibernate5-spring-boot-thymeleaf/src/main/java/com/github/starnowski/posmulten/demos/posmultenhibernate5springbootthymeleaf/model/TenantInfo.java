package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "tenant_info")
@NoArgsConstructor
@AllArgsConstructor
public class TenantInfo {

    @Id
    @Column(name = "tenant_id")
    String tenantId;

    String domain;

    //Date deletedTime
}
