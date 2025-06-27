package org.sumit.springdemo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AdminController {
    
    @GetMapping("/admin")
    public String admin(Model model) {
        return "admin"; // Returns a view name for the admin page
    }
    
}
