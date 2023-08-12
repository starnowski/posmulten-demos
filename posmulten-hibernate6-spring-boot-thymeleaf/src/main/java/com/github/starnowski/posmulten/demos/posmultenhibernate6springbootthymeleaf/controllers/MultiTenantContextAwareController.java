package com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.controllers;

import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.dto.PostDto;
import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.dto.UserDto;
import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.forms.PostForm;
import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.services.PostService;
import com.github.starnowski.posmulten.demos.posmultenhibernate6springbootthymeleaf.security.SecurityServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@Controller
@RequestMapping("/app/{domain}")
public class MultiTenantContextAwareController {

    @Autowired
    private PostService postService;
    @Autowired
    private SecurityServiceImpl securityService;

    @GetMapping(value = {"/login", "/login/"})
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

    @GetMapping(value = {"/hello", "/hello/"})
    public String getHello(
            @PathVariable("domain") String domain, Model model) {
        log.debug("String getHome()");
        model.addAttribute("domainPrefix", "/app/" + domain);
        return "hello-template";
    }

    @GetMapping(value = {"/config", "/config/"})
    public String getConfig(
            @PathVariable("domain") String domain, Model model) {
        log.debug("String getHome()");
        model.addAttribute("domainPrefix", "/app/" + domain);
        return "config-page";
    }

    @GetMapping(value = {"/posts", "/posts/"})
    public String getPosts(
            @PathVariable("domain") String domain, Model model) {
        log.debug("String getPosts()");
        model.addAttribute("domainPrefix", "/app/" + domain);
        model.addAttribute("allPosts", postService.getAllPosts());
        return "posts";
    }

    @GetMapping(value = {"/add-posts", "/add-posts/"})
    public String getAddPosts(
            @PathVariable("domain") String domain, Model model) {
        log.debug("String getPosts()");
        model.addAttribute("domainPrefix", "/app/" + domain);
        model.addAttribute("postForm", new PostForm());
        return "add-posts";
    }

    @PostMapping(value = {"/add-posts", "/add-posts/"})
    public RedirectView createPost(
            @ModelAttribute PostForm postForm) {
        postService.create(new PostDto().setText(postForm.getText()).setAuthor(new UserDto().setUserId(securityService.findLoggedInTenantUser().getUserId())));
        return new RedirectView("/app/{domain}/posts");
    }
}
