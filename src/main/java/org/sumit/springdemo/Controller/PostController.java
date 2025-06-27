package org.sumit.springdemo.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.sumit.springdemo.model.Post;
import org.sumit.springdemo.services.PostServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class PostController {

    @Autowired
    private PostServices postServices;

    @GetMapping("post/{id}")
    public String getPost(@PathVariable Long id , Model model) {
        Optional<Post> post = postServices.getById(id);
        if (post.isPresent()) {
            model.addAttribute("post", post.get());
            return "Post";
        }else{
            return "404";
        }
    }
    


}
