package com.example.space.controller;

import com.example.space.models.User;
import com.example.space.repos.PostRepository;
import com.example.space.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class MyPostController {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/my-post")
    public String showMyPost(Model model, Principal principal) {
        User user =  userRepository.findByUsername(principal.getName());
        model.addAttribute("posts", user.getPosts());
        return "user-mypost";
    }
}
