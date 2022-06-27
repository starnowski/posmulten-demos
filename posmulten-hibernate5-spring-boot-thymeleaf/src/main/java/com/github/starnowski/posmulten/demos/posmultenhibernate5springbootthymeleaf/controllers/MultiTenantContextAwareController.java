package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/app/{domain}")
public class MultiTenantContextAwareController {

    @GetMapping(value = {"/login"})
    public String getLogin(
            @PathVariable("domain") String domain, Model model) {
        log.debug("String getLogin()");
        model.addAttribute("domainPrefix", "/app/" + domain);
        return "login";
    }

    @GetMapping(value = {"/", "/home"})
    public String getHome(
            @PathVariable("domain") String domain, Model model) {
        log.debug("String getHome()");
        model.addAttribute("domainPrefix", "/app/" + domain);
        return "index";
    }

    @GetMapping(value = {"/hello"})
    public String getHello(
            @PathVariable("domain") String domain, Model model) {
        log.debug("String getHome()");
        model.addAttribute("domainPrefix", "/app/" + domain);
        return "hello-template";
    }

}
