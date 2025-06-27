package org.sumit.springdemo.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.sumit.springdemo.model.Account;
import org.sumit.springdemo.services.AccountServices;
import org.sumit.springdemo.services.EmailService;
import org.sumit.springdemo.util.mail.EmailDetail;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;




@Controller
public class AccountController {
    @Autowired
    private AccountServices accountServices;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Value("${site.domain}")
    private String domain;

    @Value("${password.token.reset.timeout.minutes}")
    private int password_Token_timeout;
    
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("account", new Account());
        return "register";
    }    

    // @Value("${password.token.timeout.minutes}")
    // private int password_Token_timeout;


    @PostMapping("/register")
    public String registerAccount(@ModelAttribute("account") @Valid Account account,
            BindingResult result,
            @RequestParam("profileImage") MultipartFile profileImage,
            Model model,
            HttpServletRequest request) {

        // if (result.hasErrors()) {
        //     // Log errors to console
        //     System.out.println("Validation errors found:");
        //     result.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));

        //     // Optional: Add errors to the model to display in the view
        //     model.addAttribute("validationErrors", result.getAllErrors());

        //     return "register";
        // }                

        // Handle file save
        if (!profileImage.isEmpty()) {
            try {
                String originalFilename = profileImage.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
                String uniqueFilename = "profile_" + System.currentTimeMillis() + extension;

                String uploadDir = request.getServletContext().getRealPath("/uploads/");
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists())
                    uploadPath.mkdirs();

                File savedFile = new File(uploadPath, uniqueFilename);
                profileImage.transferTo(savedFile);

                account.setProfileImage(uniqueFilename); // Only save filename, not full path

            } catch (IOException e) {
                model.addAttribute("imageUploadError", "Error uploading image.");
                return "register";
            }
        }

        accountServices.save(account);
        return "redirect:/login";
    }    




    @GetMapping("/login")
    public String login(Model model) {
        // This method will handle the login logic
        // It will interact with the service layer to authenticate the user
        return "login"; // Returns a view name for the login page
    }
    
    @PostMapping("/login")
    public String login_user(@RequestParam("username") String email, @RequestParam("password") String password, Model model) {
        Account account = accountServices.findByEmail(email);

        if (account != null && passwordEncoder.matches(password, account.getPassword())) {
            model.addAttribute("account", account);
            return "redirect:/profile";
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }
    
    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        Optional<Account> optionalAccount = accountServices.getOneByEmail(principal.getName());
        if (!optionalAccount.isPresent()) {
            return "redirect:/login"; // Redirect to login if account not found
        }else{
            Account account = optionalAccount.get();
            model.addAttribute("account", account);
            return "profile";
        }
        
    }
    @GetMapping("/test")
    public String test(Model model) {
        // This method will handle the test logic
        return "test"; // Returns a view name for the test page
    }


    @GetMapping("/profile/update")
    @PreAuthorize("isAuthenticated()")
    public String showProfileForm(Model model, Principal principal) {
        String email = principal.getName(); // get logged-in user's email
        Account account = accountServices.findByEmail(email);
        model.addAttribute("account", account);
        return "profileUpdate"; // the Thymeleaf template name
    }

    @PostMapping("/profile/update")
    @PreAuthorize("isAuthenticated()")
    public String updateProfile(
            @Valid @ModelAttribute("account") Account updatedAccount,
            BindingResult result,
            Principal principal,
            Model model) {
        if (result.hasErrors()) {
                                    //Here is the error 
            System.out.println("error 1");
            return "profileUpdate"; // show form again with validation errors
        }

        String email = principal.getName();
        Account existingAccount = accountServices.findByEmail(email);

        // Update only allowed fields
        existingAccount.setTitle(updatedAccount.getTitle());
        existingAccount.setFirstName(updatedAccount.getFirstName());
        existingAccount.setLastName(updatedAccount.getLastName());
        existingAccount.setDateOfBirth(updatedAccount.getDateOfBirth());
        existingAccount.setGender(updatedAccount.getGender());
        existingAccount.setAge(updatedAccount.getAge());
        existingAccount.setPassword(updatedAccount.getPassword()); // optional: hash it
        accountServices.save(existingAccount);

        return "redirect:/profile"; // go to profile page
    }


    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("account", new Account());
        return "forgot-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email,
            RedirectAttributes redirectAttributes,
            Model model) {
        System.out.println("Reset password called for email: " + email);

        Optional<Account> optionalAccount = accountServices.getOneByEmail(email);
        if (optionalAccount.isPresent()) {
            System.out.println("Account found. Generating token...");

            Account account = optionalAccount.get();
            String resetToken = UUID.randomUUID().toString();
            System.out.println("Generated token: " + resetToken); // Log the token

            account.setPasswordResetToken(resetToken);
            account.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(password_Token_timeout));
            System.out.println("Before save:");
            System.out.println("Token: " + account.getPasswordResetToken());
            System.out.println("Expiry: " + account.getPasswordResetTokenExpiry());

            accountServices.register(account);
            String messageBody = "To reset your password, please click the link below:\n" + domain + "change-password?token=" + resetToken + "&email=" + email;
            String subject = "Password Reset Request";
            System.out.println("Sending email to: " + email);
            EmailDetail emailDetail = new EmailDetail(email, subject, messageBody);
            boolean emailSent = emailService.sendSimpleEmail(emailDetail);
            if (emailSent) {
                System.out.println("Email sent successfully to " + email);
            } else {
                System.out.println("Failed to send email to " + email);
            }
            System.out.println("Saved successfully!");

            redirectAttributes.addFlashAttribute("message", "Password reset link sent to " + email);
            return "redirect:/login";
        } else {
            System.out.println("Email not found");
            model.addAttribute("error", "Email not found");
            return "forgot-password";
        }
    }   
    
    @GetMapping("/change-password")
    public String changepassword(Model model,
            @RequestParam("token") String token,
            @RequestParam("email") String email,
            RedirectAttributes attributes) {
        System.out.println("Received token: " + token);
        Optional<Account> account = accountServices.findByTokenInTable(token);

        if (account.isPresent()) {
            System.out.println("Account found for token: " + account.get().getEmail());
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(account.get().getPasswordResetTokenExpiry())) {
                System.out.println("Token expired");
                attributes.addFlashAttribute("error", "Token Expired");
                return "redirect:/forgot-password";
            }

            model.addAttribute("accountid", account.get().getId());
            model.addAttribute("email", email); // Optional: pass to form
            model.addAttribute("token", token); // Optional: pass to form
            return "change-password";
        }

        System.out.println("Token not found in DB!");
        attributes.addFlashAttribute("error", "Token Invalid");
        return "redirect:/forgot-password";
    }

    @PostMapping("/change-password")
    public String handlePasswordReset(
            @RequestParam("token") String token,
            @RequestParam("email") String email,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {

        Optional<Account> optionalAccount = accountServices.findByTokenInTable(token);

        if (optionalAccount.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired token.");
            return "redirect:/change-password?token=" + token + "&email=" + email;
        }

        Account account = optionalAccount.get();

        if (!account.getEmail().equals(email)) {
            redirectAttributes.addFlashAttribute("error", "Invalid email for this token.");
            return "redirect:/change-password?token=" + token + "&email=" + email;
        }

        if (account.getPasswordResetTokenExpiry() != null &&
                LocalDateTime.now().isAfter(account.getPasswordResetTokenExpiry())) {
            redirectAttributes.addFlashAttribute("error", "Token expired.");
            return "redirect:/forgot-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/change-password?token=" + token + "&email=" + email;
        }

        account.setPassword(newPassword); // will be encoded inside accountServices.register()
        account.setPasswordResetToken(null);
        account.setPasswordResetTokenExpiry(null);
        accountServices.register(account);

        redirectAttributes.addFlashAttribute("message", "Password changed successfully. You can now log in.");
        return "redirect:/login";
    }
    
    
    
}
