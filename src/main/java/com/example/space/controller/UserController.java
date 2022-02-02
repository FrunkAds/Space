package com.example.space.controller;

import com.example.space.models.User;
import com.example.space.repos.PostRepository;
import com.example.space.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping
    public String showUserPage(Model model, Principal principal) {

        model.addAttribute("user", userRepository.findByUsername(principal.getName()));
        return "user-page";
    }

    @GetMapping("/edit")
    public String userEdit(Model model, Principal principal) {
        model.addAttribute("user", userRepository.findByUsername(principal.getName()));
        return "user-edit";
    }

    @PostMapping("/edit")
    public String userEditPost(@RequestParam("firstName") String firstName,
                               @RequestParam("lastName") String lastName,
                               @RequestParam("email") String email,
                               Principal principal,
                               final @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        User user = userRepository.findByUsername(principal.getName());
        if (file.isEmpty()){
            user.setFilename(user.getFilename());
        }else {

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + " " + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFileName));
            user.setFilename(resultFileName);
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        userRepository.save(user);
        return "redirect:/user";
    }


}
