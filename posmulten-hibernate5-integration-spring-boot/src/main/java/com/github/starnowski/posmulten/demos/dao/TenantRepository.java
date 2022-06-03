package com.github.starnowski.posmulten.demos.dao;

import com.github.starnowski.posmulten.demos.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, String> {
}
