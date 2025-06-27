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
                        .anyRequest().permitAll() // ✅ Allow all endpoints
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .rememberMe(remember -> remember
                        .key("uniqueAndSecretKey")
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(1209600))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/home")
                        .permitAll())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/db-console/**") // Allow CSRF for H2 console
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()) // Allow H2 console in iframe
                );

      // .authorizeHttpRequests(auth -> auth
                // .requestMatchers(
                //         "/",
                //         "/home",
                //         "/login",
                //         "/register",
                //         "/forgot-password",
                //         "/css/**",
                //         "/js/**",
                //         "/images/**",
                //         "/webjars/**",
                //         "/db-console/**")
                // .permitAll()
                // .requestMatchers("/Profile", "/Profile/**").authenticated()
                // .requestMatchers("/admin/**").hasRole("ADMIN")
                // .requestMatchers("/editor/**").hasAnyRole("ADMIN", "EDITOR")
                // .requestMatchers("/test/**")
                // .hasAuthority(Authorities.ACCESS_ADMIN_PANEL.getAuthorityString())
                // .anyRequest().authenticated())
                // .formLogin(form -> form
                // .loginPage("/login")
                // .usernameParameter("username")
                // .passwordParameter("password")
                // .defaultSuccessUrl("/home", true)
                // .failureUrl("/login?error=true")
                // .permitAll())
                // // ✅ Enable Remember-Me here
                // .rememberMe(remember -> remember
                // .key("uniqueAndSecretKey") // can be any random string
                // .rememberMeParameter("remember-me") // this should match the checkbox
                // // name
                // // in your HTML
                // .tokenValiditySeconds(1209600) // 14 days
                // )
                // .logout(logout -> logout
                // .logoutUrl("/logout")
                // .logoutSuccessUrl("/home")
                // .permitAll())
                // .csrf(csrf -> csrf
                // .ignoringRequestMatchers("/db-console/**"))
                // .headers(headers -> headers
                // .frameOptions(frame -> frame.disable()));

        return http.build();
    }

}
