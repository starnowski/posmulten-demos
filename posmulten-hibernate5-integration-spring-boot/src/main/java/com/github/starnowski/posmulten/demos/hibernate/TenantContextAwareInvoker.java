package com.github.starnowski.posmulten.demos.hibernate;

import com.github.starnowski.posmulten.demos.hibernate.exceptions.InvalidTenantContext;
import com.github.starnowski.posmulten.hibernate.core.context.CurrentTenantContext;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import static java.lang.String.format;

@Component
public class TenantContextAwareInvoker {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private TransactionService transactionService;

    public <R,E extends Exception> R tryExecutedInCorrectTenantContext(SupplierWithGenericException<R,E> supplier, String tenant) throws E{
        assertSessionNotOpenedForOtherTenant(tenant);
        String previousTenant = CurrentTenantContext.getCurrentTenant();
        try {
            CurrentTenantContext.setCurrentTenant(tenant);
            return supplier.get();
        } finally {
            CurrentTenantContext.setCurrentTenant(previousTenant);
        }
    }

    public <R,E extends Exception> R tryExecuteTransactionInCorrectTenantContext(SupplierWithGenericException<R,E> supplier, String tenant) throws E{
        assertSessionNotOpenedForOtherTenant(tenant);
        String previousTenant = CurrentTenantContext.getCurrentTenant();
        try {
            CurrentTenantContext.setCurrentTenant(tenant);
            return transactionService.executeTransaction(supplier);
        } finally {
            CurrentTenantContext.setCurrentTenant(previousTenant);
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
            return (SessionImplementor) sessionFactory.getCurrentSession();
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