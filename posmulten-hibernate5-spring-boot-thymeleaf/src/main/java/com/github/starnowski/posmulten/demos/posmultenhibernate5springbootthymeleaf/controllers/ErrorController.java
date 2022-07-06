package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.controllers;

import com.github.starnowski.posmulten.hibernate.core.context.CurrentTenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import static com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.security.TenantUser.ROOT_TENANT_ID;

@Slf4j
@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String handleError(Model model, HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String currentTenant = CurrentTenantContext.getCurrentTenant();
        log.trace("current Tenant: " + currentTenant);
        if(ROOT_TENANT_ID.equals(currentTenant)) {
            model.addAttribute("tenant", false);
        } else {
            model.addAttribute("tenant", true);
        }

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            log.warn("status: " + statusCode);
        }
        return "errors/error";
    }
}
