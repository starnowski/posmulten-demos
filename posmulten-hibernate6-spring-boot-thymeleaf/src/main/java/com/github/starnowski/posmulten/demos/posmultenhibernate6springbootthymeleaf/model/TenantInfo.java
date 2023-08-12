package com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


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
