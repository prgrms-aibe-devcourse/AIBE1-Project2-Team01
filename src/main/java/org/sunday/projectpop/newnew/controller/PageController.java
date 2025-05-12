package org.sunday.projectpop.newnew.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/auth/signin")
    public String signinPage() {
        return "signin";
    }

    @GetMapping("/auth/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/profile/view")
    @PreAuthorize("isAuthenticated()")
    public String viewPage() {
        return "profile-view";
    }

    @GetMapping("/profile/edit")
    @PreAuthorize("isAuthenticated()")
    public String editPage() {
        return "profile-edit";
    }

    @GetMapping("/profile/new")
    public String profilePage(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/signin";
        }
        return "profile-new";
    }

}

