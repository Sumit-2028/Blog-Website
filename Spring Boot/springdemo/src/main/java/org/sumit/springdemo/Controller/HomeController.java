package org.sumit.springdemo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.sumit.springdemo.model.Post;
import org.sumit.springdemo.services.PostServices;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HomeController {

    @Autowired
    private PostServices postServices;

    /**
     * Handles GET request to /home
     * If the user is authenticated:
     * - Fetches blog posts
     * - Sends them to the view (home.html)
     * If not authenticated:
     * - Does not send posts
     * - View will show login message (Thymeleaf handles this)
     */
    @GetMapping("/home")
    public String home(Model model) {
        // Get the current authenticated user (or anonymous user)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is logged in and not anonymous
        boolean isAuthenticated = auth != null &&
                auth.isAuthenticated() &&
                !"anonymousUser".equals(auth.getPrincipal());

        // Send authentication status to Thymeleaf view
        model.addAttribute("isAuthenticated", isAuthenticated);

        // If authenticated, fetch blog posts and send them to the view
        if (isAuthenticated) {
            List<Post> posts = postServices.getAll();
            model.addAttribute("posts", posts);
        }

        // Return home.html
        return "home";
    }

    /**
     * Handles GET request to /editor
     * Just returns editor.html
     */
    @GetMapping("/editor")
    public String editor() {
        return "editor";
    }
}
