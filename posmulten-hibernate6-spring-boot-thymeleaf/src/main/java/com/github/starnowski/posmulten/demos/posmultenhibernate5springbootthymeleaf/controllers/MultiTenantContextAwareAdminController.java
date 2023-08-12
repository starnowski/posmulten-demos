package com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.controllers;

import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.dto.UserDto;
import com.github.starnowski.posmulten.demos.posmultenhibernate5springbootthymeleaf.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@Controller
public class MultiTenantContextAwareAdminController {

    @Autowired
    private UserService userService;

    @GetMapping(value = {"/app/{domain}/users", "/app/{domain}/users/"})
    public String getUsers(
            @PathVariable("domain") String domain, Model model) {
        log.debug("String getUsers()");
        model.addAttribute("domainPrefix", "/app/" + domain);
        model.addAttribute("allUsers", userService.getAllUsers());
        return "users";
    }

    @GetMapping(value = {"/app/{domain}/add-users", "/app/{domain}/add-users/"})
    public String getAddUsers(
            @PathVariable("domain") String domain, Model model) {
        log.debug("String getAddUsers()");
        model.addAttribute("domainPrefix", "/app/" + domain);
        model.addAttribute("userForm", new UserDto());
        return "add-users";
    }

    @PostMapping(value = {"/app/{domain}/add-users", "/app/{domain}/add-users/"})
    public RedirectView createUsers(
            @ModelAttribute UserDto userDto) {
        userService.create(userDto);
        return new RedirectView("/app/{domain}/users");
    }
}
