package org.sumit.springdemo.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.sumit.springdemo.model.Post;
import org.sumit.springdemo.services.PostServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller // MVC Controller that handles HTTP requests
public class HomeController {

    @Autowired
    private PostServices postServices;

    @GetMapping("/home") // Maps HTTP GET /home to this method
    public String home(Model model) {
        List<Post> posts = postServices.getAll(); // Fetch all posts
        model.addAttribute("posts", posts); // Add posts to the model for Thymeleaf template
        return "home"; // Return view name "home.html"
    }

    @GetMapping("/editor")
    public String editor() {
        return "editor"; 
    }

}
