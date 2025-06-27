package org.sumit.springdemo.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.sumit.springdemo.model.Account;
import org.sumit.springdemo.model.Post;
import org.sumit.springdemo.services.AccountServices;
import org.sumit.springdemo.services.PostServices;

import jakarta.validation.Valid;

@Controller
public class PostController {

    @Autowired
    private PostServices postServices;

    @Autowired
    private AccountServices accountServices;

    @Autowired
    private org.sumit.springdemo.repositories.AccountRepository accountRepository;
    @GetMapping("post/{id}")
    @PreAuthorize("isAuthenticated()") // Ensure the user is authenticated
    public String getPost(@PathVariable Long id, Model model, Principal principal) {
        Optional<Post> optionalPost = postServices.getById(id);

        if (!optionalPost.isPresent()) {
            return "404"; // Post not found
        }

        Post post = optionalPost.get();
        model.addAttribute("post", post);

        // Default author email to something invalid
        String authUser = "anonymous";

        // Check if user is logged in
        if (principal != null) {
            authUser = principal.getName();
        }

        // Check if the post's account and email match
        if (post.getAccount() != null && authUser.equals(post.getAccount().getEmail())) {
            model.addAttribute("isAuthor", true);
        } else {
            model.addAttribute("isAuthor", false);
        }

        return "Post";
    }

    @GetMapping("/AddPost")
    @PreAuthorize("isAuthenticated()")
    public String showAddPostForm(Model model, Principal principal) {
        Optional<Account> account = accountServices.getOneByEmail(principal.getName());
        if (account.isPresent()) {
            Post post = new Post();
            post.setAccount(account.get());
            model.addAttribute("post", post);
            model.addAttribute("account", account.get().getEmail());
            return "AddPost";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/AddPost")
    @PreAuthorize("isAuthenticated()")
    public String handleAddPost(@ModelAttribute("post") @Valid Post post,
            BindingResult result,
            @RequestParam("image") MultipartFile file,
            Principal principal,
            Model model) {

        // if (result.hasErrors()) {
        //     System.out.println("Post body received: '" + post.getBody() + "'");
        //     return "AddPost";
        // }                

        try {
            if (!file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                String uploadDir = new File("src/main/resources/static/uploads").getAbsolutePath();
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists())
                    uploadPath.mkdirs();

                Path filePath = Paths.get(uploadDir, fileName);
                Files.write(filePath, file.getBytes());

                post.setImage("/uploads/" + fileName);
            }

            Optional<Account> account = accountServices.getOneByEmail(principal.getName());
            account.ifPresent(post::setAccount);

            post.setCreatedAt(java.time.LocalDateTime.now());
            postServices.save(post);
            return "redirect:/home";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Image upload failed");
            return "AddPost";
        }
    }
    @GetMapping("/post/{id}/delete")
    @PreAuthorize("isAuthenticated()") // Ensure the user is authenticated
    public String deletepost(@PathVariable Long id, Model model, Principal principal) {
        Optional<Post> optionalPost = postServices.getById(id);

        if (!optionalPost.isPresent()) {
            return "404"; // Post not found
        }else{
            Post post = optionalPost.get();
            if (post.getAccount().getEmail().equals(principal.getName())) {
                postServices.delete(post);
                return "redirect:/home"; // Redirect to home after deletion
            } else {
                return "redirect:/post/" + id; // Redirect to the post if not the author
            }
        }
    }

    @GetMapping("/post/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String editPost(@PathVariable Long id, Model model, Principal principal) {
        Optional<Post> optionalPost = postServices.getById(id);
        if (!optionalPost.isPresent()) {
            return "404";
        }

        Post post = optionalPost.get();

        // Only author can edit
        if (!post.getAccount().getEmail().equals(principal.getName())) {
            return "redirect:/post/" + id;
        }

        model.addAttribute("post", post);
        return "EditPost"; 
    }


    @PostMapping("/update_post/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updatePost(@PathVariable Long id,
            @ModelAttribute("post") @Valid Post updatedPost,
            BindingResult result,
            Principal principal,
            Model model) {
        Optional<Post> optionalPost = postServices.getById(id);
        if (!optionalPost.isPresent()) {
            return "404";
        }

        Post existingPost = optionalPost.get();

        // Only author can update
        if (!existingPost.getAccount().getEmail().equals(principal.getName())) {
            return "403"; // Forbidden
        }

        if (result.hasErrors()) {
            model.addAttribute("post", updatedPost);
            return "EditPost";
        }

        // Update fields
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setBody(updatedPost.getBody());
        existingPost.setCreatedAt(java.time.LocalDateTime.now());

        postServices.save(existingPost);

        return "redirect:/post/" + id;
    }

}
