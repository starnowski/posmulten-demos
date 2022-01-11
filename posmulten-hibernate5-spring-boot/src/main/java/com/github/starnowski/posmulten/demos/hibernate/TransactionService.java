package com.github.starnowski.posmulten.demos.hibernate;

import com.github.starnowski.posmulten.demos.hibernate.TenantContextAwareInvoker.SupplierWithGenericException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionService {

    @Transactional
    public <R, E extends Exception> R executeTransaction(SupplierWithGenericException<R, E> supplier) throws E
    {
        return supplier.get();
    }
}
