package org.sumit.springdemo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.sumit.springdemo.model.Account;
import org.sumit.springdemo.services.AccountServices;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class AccountController {
    @Autowired
    private AccountServices accountServices;
    @GetMapping("/register")
    public String register(Model model) {
        // This method will handle the registration logic
        // It will interact with the service layer to save the account details
        Account account = new Account();
        // Populate the account object with data from the model or request parameters
        model.addAttribute("account", account);
        // Here you would typically call a service to save the account
        return "register"; // Returns a view name or redirect
    }


    @PostMapping("/register")
    public String register_user(@ModelAttribute Account account, Model model) {
        // This method will handle the registration logic
        // It will interact with the service layer to save the account details
        // Here you would typically call a service to save the account
        accountServices.save(account); // Assuming AccountServices has a static save method
        return "redirect:/home"; // Redirects to login page after successful registration
    }

    @GetMapping("/login")
    public String login(Model model) {
        // This method will handle the login logic
        // It will interact with the service layer to authenticate the user
        return "login"; // Returns a view name for the login page
    }
    @GetMapping("/profile")
    public String profile(Model model) {
        // This method will handle the login logic
        // It will interact with the service layer to authenticate the user
        return "profile"; // Returns a view name for the login page
    }

    @GetMapping("/test")
    public String test(Model model) {
        // This method will handle the test logic
        return "test"; // Returns a view name for the test page
    }
    
}
