package com.example.space.controller;

import com.example.space.models.AuthenticationProvider;
import com.example.space.models.Role;
import com.example.space.models.User;
import com.example.space.repos.UserRepository;
import com.example.space.service.MailSender;
import com.example.space.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.UUID;

@Controller
public class SecurityController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MailSender mailSender;

    @GetMapping("/registration")
    public String registration(Model model) {
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam String email,
                          Model model,
                          User user) {
        User userFromDb = userRepository.findByUsername(user.getUsername());
        if (userFromDb != null) {
            model.addAttribute("message", "User exists!");
            return "registration";
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setFilename("unknow.png");
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setAuthenticationProvider(AuthenticationProvider.LOCAL);
        user.setActivationCode(UUID.randomUUID().toString());
        userRepository.save(user);

        if(!StringUtils.isEmpty(user.getEmail())){
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to Space. Please, visit next link: http://localhost:3333/activate/%s",
                    user.getUsername(),
                    user.getActivationCode()
            );

            mailSender.send(user.getEmail(), "Activation code", message);
        }
        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String sendActivateCode(@PathVariable String code, Model model){
        boolean isActivated = userService.activeUser(code);

        if (isActivated){
            model.addAttribute("message", "User successfully activated");
        }else {
            model.addAttribute("message", "Activation code is not found");
        }

        return "login";
    }

}

