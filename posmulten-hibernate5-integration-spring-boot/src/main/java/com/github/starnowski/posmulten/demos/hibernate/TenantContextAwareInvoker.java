package com.github.starnowski.posmulten.demos.hibernate;

import com.github.starnowski.posmulten.demos.hibernate.exceptions.InvalidTenantContext;
import com.github.starnowski.posmulten.demos.util.TenantContext;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static java.lang.String.format;

@Component
public class TenantContextAwareInvoker {

    @PersistenceContext(unitName = "pu")
    private EntityManager entityManager;

    @Autowired
    private TransactionService transactionService;

    public <R,E extends Exception> R tryExecutedInCorrectTenantContext(SupplierWithGenericException<R,E> supplier, String tenant) throws E{
        assertSessionNotOpenedForOtherTenant(tenant);
        String previousTenant = TenantContext.getCurrentTenant();
        try {
            TenantContext.setCurrentTenant(tenant);
            return supplier.get();
        } finally {
            TenantContext.setCurrentTenant(previousTenant);
        }
    }

    public <R,E extends Exception> R tryExecuteTransactionInCorrectTenantContext(SupplierWithGenericException<R,E> supplier, String tenant) throws E{
        assertSessionNotOpenedForOtherTenant(tenant);
        String previousTenant = TenantContext.getCurrentTenant();
        try {
            TenantContext.setCurrentTenant(tenant);
            return transactionService.executeTransaction(supplier);
        } finally {
            TenantContext.setCurrentTenant(previousTenant);
        }
    }

    private void assertSessionNotOpenedForOtherTenant(String tenant) {
        SessionImplementor sessionImplementor = tryReturnSessionImplementorIfExists();
        if (sessionImplementor != null && sessionImplementor.isOpen() && !StringUtils.equals(tenant, sessionImplementor.getTenantIdentifier())) {
            throw new InvalidTenantContext(format("Session opened for tenant %s but expected tenant %s ", sessionImplementor.getTenantIdentifier(), tenant));
        }
    }

    private SessionImplementor tryReturnSessionImplementorIfExists()
    {
        try {
            return entityManager.unwrap(SessionImplementor.class);
        }
        catch (Exception ex)
        {
            return null;
        }
    }


    public interface SupplierWithGenericException<R, E extends Exception> {

        R get() throws E;
    }
}