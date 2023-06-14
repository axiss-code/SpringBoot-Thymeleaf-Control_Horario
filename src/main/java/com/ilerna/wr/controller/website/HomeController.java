package com.ilerna.wr.controller.website;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
@Slf4j
public class HomeController {
    
    //Redirije al INICIO correspondiente a cada ROL
    @GetMapping("")
    public String home(@RequestParam(defaultValue = "false", required = false) String error, Model model) {
         if (error.equals("true")) {
            model.addAttribute("error", "Email/Password incorrecto.");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info(auth.getAuthorities().toString());
        switch (auth.getAuthorities().toString()) {
            case "[ROLE_ADMIN]":
                return "redirect:/admin";
            case "[ROLE_EDITOR]":
                return "index";
            case "[ROLE_USER]":
                return "redirect:/user";
            case "[ROLE_ANONYMOUS]":
                return "index";
            default:
                return "index";
        }
    }
    
    //Página de errores
    @GetMapping("/error")
    public String error() {
        return "error";
    }
    
    //Página de logueo
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    //Redirije al HOME cuando se desloguea un usuario
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }

}
