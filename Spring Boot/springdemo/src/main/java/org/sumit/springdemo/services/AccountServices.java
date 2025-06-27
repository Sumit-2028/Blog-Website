package org.sumit.springdemo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.sumit.springdemo.model.Account;
import org.sumit.springdemo.model.Authority;
import org.sumit.springdemo.repositories.AccountRepository;
import org.sumit.springdemo.util.constants.Roles;

@Service
public class AccountServices implements UserDetailsService {
    // This service class is responsible for handling account-related operations

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;
    // This service class can contain

    public Account save(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        if (account.getRole() == null || account.getRole().isEmpty()) {
            account.setRole(Roles.USER.getRole());
        }
        return accountRepository.save(account);
    }

    public Account register(Account account) {
        return accountRepository.save(account); // no password encoding here
    }    

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = new ArrayList<>();

        // Ensure this line is present to make ROLE_ADMIN recognized
        authorities.add(new SimpleGrantedAuthority(account.getRole()));

        // Optional: include any extra authorities
        for (Authority auth : account.getAuthorities()) {
            authorities.add(new SimpleGrantedAuthority(auth.getName())); // if any
        }

        return new org.springframework.security.core.userdetails.User(
                account.getEmail(),
                account.getPassword(),
                authorities);
    }

    public Optional<Account> getOneByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public Account findByEmailAndPassword(String email, String password) {
        // TODO Auto-generated method stub
        return accountRepository.findByEmailAndPassword(email, password);
    }

    public Account findByEmail(String name) {
        // TODO Auto-generated method stub
        return accountRepository.findByEmail(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + name));
    }

    public Optional<Account> findById(Long id) {
        // TODO Auto-generated method stub
       return accountRepository.findById(id);
    }

    public Optional<Account> findByTokenInTable(String token) {
        return accountRepository.findByPasswordResetToken(token); // ✅ fixed
    }
   

}
