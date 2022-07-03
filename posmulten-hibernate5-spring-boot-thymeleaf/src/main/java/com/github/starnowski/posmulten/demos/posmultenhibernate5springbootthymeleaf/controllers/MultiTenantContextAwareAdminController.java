package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.controllers;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class MultiTenantContextAwareAdminController {

    @Autowired
    private UserService userService;

    @GetMapping(value = {"/app/{domain}/admin"})
    public String getUsers(
            @PathVariable("domain") String domain, Model model) {
        log.debug("String getUsers()");
        model.addAttribute("domainPrefix", "/app/" + domain);
        model.addAttribute("allUsers", userService.getAllUsers());
        return "posts";
    }
    //config-page
}
