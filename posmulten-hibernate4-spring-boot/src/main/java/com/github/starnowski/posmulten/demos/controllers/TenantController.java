package com.github.starnowski.posmulten.demos.controllers;

import com.github.starnowski.posmulten.demos.dto.TenantDto;
import com.github.starnowski.posmulten.demos.hibernate.TenantContextAwareInvoker;
import com.github.starnowski.posmulten.demos.services.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@ControllerAdvice
@RequestMapping("/app/tenants")
public class TenantController {

    @Autowired
    private AsyncTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private TenantContextAwareInvoker tenantContextAwareInvoker;

    @Autowired
    private TenantService tenantService;

    @PostMapping
    public ResponseEntity<TenantDto> createTenant(@RequestBody TenantDto body) throws InterruptedException, ExecutionException {
        ResponseEntity responseEntity;
        /*
         *
         * In case when the active tenant is one of functional account {@link TenantContext#PRIVILEGED_TENANT_ID} or {@link TenantContext#NON_PRIVILEGED_TENANT_ID}  then
         * the action of domain creation must be executed in new thread where there is set correct tenant for new domain.
         * The reason why this invocation must be done in new thread is that at this moment we can not change current tenant
         * for hibernate session in current thread.
         * Please see few links related to this issue:
         * https://stackoverflow.com/questions/30757344/hibernate-multitenancy-change-tenant-in-session
         * https://hibernate.atlassian.net/browse/HHH-9766
         *
         * (Good explanation how hibernate session is created)
         * https://developer.atlassian.com/server/confluence/hibernate-sessions-and-transaction-management-guidelines/
         *
         * In other case when functional tenant is not active then the method {@link DomainRegisterServic#createNewDomain) is
         * executed in that same thread.
         */
        Future<TenantDto> future = threadPoolTaskExecutor.submit(() -> tenantContextAwareInvoker.tryExecutedInCorrectTenantContext(() -> tenantService.create(body), body.getName()));
        responseEntity = new ResponseEntity(future.get(), HttpStatus.ACCEPTED);
        return responseEntity;
    }
}
