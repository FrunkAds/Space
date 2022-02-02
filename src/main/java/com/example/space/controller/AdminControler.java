package com.example.space.controller;

import com.example.space.models.User;
import com.example.space.repos.UserRepository;
import com.example.space.service.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminControler {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailSender mailSender;
    @Value("${upload.path}")
    private String uploadPath;


    @GetMapping("/user/list")
    public String showUserList(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "user-list";
    }

    @GetMapping("/user/list/show-user-page/{user}")
    public String showUser(@PathVariable User user, Model model) {
        model.addAttribute("usr", user);
        return "user-show";
    }

    @GetMapping("/user/list/edit/{user}")
    public String editUser(@PathVariable User user, Model model) {

        model.addAttribute("user", user);
        return "admin-edit";
    }

    @PostMapping("/user/edit/{user}")
    public String editUser(@PathVariable User user,
                           @RequestParam("username") String username,
                           @RequestParam("firstName") String firstName,
                           @RequestParam("lastName") String lastName,
                           @RequestParam("email") String email,
                           final @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        user.setEmail(email);
        user.setLastName(lastName);
        user.setFirstName(firstName);
        user.setUsername(username);
        if(file.isEmpty()){
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
        userRepository.save(user);
        return"redirect:/admin/user/list";
    }

    @PostMapping("/user/list/{user}/remove")
    public String removeUser(@PathVariable User user) {
        userRepository.delete(user);
        return "redirect:/admin/user/list";
    }

    @GetMapping("/send/mail")
    public String sendMail(Model model) {
        return "send-mail";
    }

    @PostMapping("/send/mail")
    public String sendMail(@RequestParam("subject") String subject,
                           @RequestParam("message") String message,
                           @RequestParam("link") String link,
                           @AuthenticationPrincipal User user) {

        List<User> users = userRepository.findAll();
        users.remove(user);
        for (User u : users) {
            if (!StringUtils.isEmpty(u.getEmail())) {
                String mes = String.format(
                        "Привіт, %s! \n" +
                                "%s: %s",
                        u.getUsername(),
                        message,
                        link
                );

                mailSender.send(u.getEmail(), subject, mes);
            }
        }


        return "redirect:/admin/user/list";
    }

}
