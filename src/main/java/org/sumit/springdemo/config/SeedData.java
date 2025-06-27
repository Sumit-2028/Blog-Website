package org.sumit.springdemo.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.sumit.springdemo.model.Account;
import org.sumit.springdemo.model.Authority;
import org.sumit.springdemo.model.Post;
import org.sumit.springdemo.services.AccountServices;
import org.sumit.springdemo.services.AuthorityService;
import org.sumit.springdemo.services.PostServices;
import org.sumit.springdemo.util.constants.Authorities;
import org.sumit.springdemo.util.constants.Roles;

@Component  // Spring runs this on app startup
public class SeedData implements CommandLineRunner {

    @Autowired
    private PostServices postServices;
    
    @Autowired
    private AccountServices accountServices;  // Injected properly now

    @Autowired
    private AuthorityService authorityServices;  // Assuming you have an AuthorityServices class
    
    @Override
    public void run(String... args) throws Exception {

        for(Authorities authority : Authorities.values()) {
            // Save each authority to the database
            // Assuming you have an Authority entity and repository
            // authorityRepository.save(authority);
            Authority auth = new Authority();
            auth.setId(authority.getAuthorityId());
            auth.setName(authority.getAuthorityString());
            // Assuming you have an AuthorityRepository to save the authority
            authorityServices.save(auth);  // Save authority using the service

        }   

        Account account01 = new Account();
        account01.setEmail("Admin@gmail.com");
        account01.setPassword("admin123");
        account01.setFirstName("Admin");
        account01.setLastName("User");
        account01.setRole(Roles.USER.getRole());  // Set role to USER
        accountServices.save(account01);  // Now accountServices is not null

        Account account02 = new Account();
        account02.setEmail("User@gmail.com");
        account02.setPassword("user123");
        account02.setFirstName("User");
        account02.setLastName("Example");
        account02.setRole(Roles.ADMIN.getRole());  // Set role to ADMIN
        accountServices.save(account02);  // Save to the database

        Account account03 = new Account();
        account03.setEmail("Guest@gmail.com");
        account03.setPassword("guest123");
        account03.setFirstName("Guest");
        account03.setLastName("Visitor");
        account03.setRole(Roles.EDITOR.getRole());  // Set role to EDITOR
        Set<Authority> authorities = new HashSet<>(); // Get all authorities
        authorityServices.findById(Authorities.RESET_ANY_USER_PASSWORD.getAuthorityId()).ifPresent(authorities::add); // Assuming 1L is the ID for USER authority
        authorityServices.findById(Authorities.ACCESS_ADMIN_PANEL.getAuthorityId()).ifPresent(authorities::add); // Assuming 2L is the ID for ADMIN authority
        account03.setAuthorities(authorities); // Set authorities for the account
        accountServices.save(account03);  // Save to the database


        List<Post> posts = postServices.getAll();
        if (posts.isEmpty()) {  // If database is empty, add sample posts
            Post post1 = new Post();
            post1.setAccount(account01);  // Associate with the first account
            post1.setTitle("First Post");
            post1.setBody("This is the body of the first post.");
            postServices.save(post1);

            Post post2 = new Post();
            post2.setAccount(account02);  // Associate with the second account
            post2.setTitle("Second Post");
            post2.setBody("This is the body of the second post.");
            postServices.save(post2);

            Post post3 = new Post();
            post3.setAccount(account03);  // Associate with the third account
            post3.setTitle("Third Post");
            post3.setBody("This is the body of the third post.");
            postServices.save(post3);
        }
    }
}
