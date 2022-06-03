package com.github.starnowski.posmulten.demos.services;

import com.github.starnowski.posmulten.demos.dao.TenantRepository;
import com.github.starnowski.posmulten.demos.dto.TenantDto;
import com.github.starnowski.posmulten.demos.model.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Transactional
    public TenantDto create(TenantDto dto)
    {
        Tenant tenant = new Tenant();
        tenant.setName(dto.getName());
        tenant = tenantRepository.save(tenant);
        TenantDto result = new TenantDto();
        result.setName(tenant.getName());
        return result;
    }

    @Transactional(readOnly = true)
    public TenantDto findByName(String name)
    {
        Tenant tenant = tenantRepository.findById(name).get();
        return tenant == null ? null : new TenantDto().setName(tenant.getName());
    }
}
