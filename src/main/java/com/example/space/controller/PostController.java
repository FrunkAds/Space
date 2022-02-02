package com.example.space.controller;

import com.example.space.models.Post;
import com.example.space.models.Role;
import com.example.space.repos.PostRepository;
import com.example.space.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;


@Controller
@RequestMapping("/user/post")
public class PostController {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping()
    public String showAllPost(Model model) {
        Iterable<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        return "post-show";
    }

    @GetMapping("/add")
    public String addPage(Model model) {
        return "add-post";
    }

    @PostMapping("/add")
    public String addPost(@RequestParam String title,
                          @RequestParam String anons,
                          @RequestParam String fullText,
                          Principal principal,
                          @RequestParam("file") MultipartFile file) throws IOException {
        Post post = new Post(title, anons, fullText);
        post.setDate();
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        String uuidFile = UUID.randomUUID().toString();
        String resultFileName = uuidFile + " " + file.getOriginalFilename();
        file.transferTo(new File(uploadPath + "/" + resultFileName));
        post.setFilename(resultFileName);

        post.setUser(userRepository.findByUsername(principal.getName()));
        postRepository.save(post);
        return "redirect:/user/post";
    }

    @GetMapping("/{id}")
    private String showPostDetails(@PathVariable(name = "id") long id, Model model, Principal principal) {
        if (!postRepository.existsById(id)) {
            return "redirect:/user/post";
        }
        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("posts", res);
        model.addAttribute("user", userRepository.findByUsername(principal.getName()));
        model.addAttribute("admin", Role.ADMIN);
        return "post-details";
    }


    @PostMapping("/{id}/remove")
    public String removePost(@PathVariable(value = "id") long id) {
        if (!postRepository.existsById(id)) {
            return "redirect:/user/post";
        }
        postRepository.delete(postRepository.findById(id).get());
        return "redirect:/user/post";
    }

    @GetMapping("/{id}/edit")
    public String editPost(@PathVariable(value = "id") long id,
                           Model model) {
        if (!postRepository.existsById(id)) {
            return "redirect:/user/post";
        }
        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("post", res);
        return "post-edit";
    }

    @PostMapping("/{id}/edit")
    public String editPost(@PathVariable(value = "id") long id,
                               @RequestParam String title,
                               @RequestParam String anons,
                               @RequestParam String fullText,
                               final @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(title);
        post.setAnons(anons);
        post.setFullText(fullText);
        if (file.isEmpty()){
            post.setFilename(post.getFilename());
        }else {

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + " " + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFileName));
            post.setFilename(resultFileName);

        }
        postRepository.save(post);
        return "redirect:/user/post";
    }
}
