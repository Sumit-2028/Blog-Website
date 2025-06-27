package org.sumit.springdemo.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.sumit.springdemo.util.constants.Authorities;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/home",
                                "/login",
                                "/register",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/db-console/**" // ✅ allow access to H2 console
                        ).permitAll()

                        // ✅ authenticated user can access profile
                        .requestMatchers("/Profile", "/Profile/**").authenticated()

                        // ✅ Only ADMIN role can access /admin/**
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // ✅ ADMIN and EDITOR roles can access /editor/**
                        .requestMatchers("/editor/**").hasAnyRole("ADMIN", "EDITOR")

                        // ✅ Requires custom authority for /admin (e.g., button or view)
                        .requestMatchers("/test/**").hasAuthority(Authorities.ACCESS_ADMIN_PANEL.getAuthorityString())

                        // ✅ any other request must be authenticated
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/home")
                        .permitAll())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/db-console/**") // ✅ disable CSRF for H2 console
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()) // ✅ allow iframe for H2 console
                );

        return http.build();
    }
}
