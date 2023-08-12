package com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.repositories;

import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.model.TenantInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantInfoRepository extends JpaRepository<TenantInfo, String> {

    TenantInfo findByDomain(String domain);

    TenantInfo findByTenantId(String tenantId);
}
