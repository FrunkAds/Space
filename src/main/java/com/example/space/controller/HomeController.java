package com.example.space.controller;

import com.example.space.models.Role;
import com.example.space.repos.UserRepository;
import com.example.space.service.ParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {
    private final ParsingService parsingService;
    @Autowired
    private UserRepository userRepository;

    public HomeController(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    @GetMapping(value = {"/home"})
    public String showHomePage(Model model){
        model.addAttribute("article", parsingService.getAllArticle());
        return "home";
    }

    @GetMapping(value = {"/user/home","/"})
    public String userShowHomePage(Model model, Principal principal){
        model.addAttribute("user",userRepository.findByUsername(principal.getName()));
        model.addAttribute("admin", Role.ADMIN);
        model.addAttribute("article", parsingService.getAllArticle());
        return "user-index";
    }
}
