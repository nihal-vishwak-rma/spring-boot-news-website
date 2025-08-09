package com.news.SpringBootNews_wb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.news.SpringBootNews_wb.AllHandler.CustomLoginHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomLoginHandler success;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth ->

                auth
                        .requestMatchers("/", "/user/resendotp", "/user/resetpassword", "/user/updatepassword",
                                "/user/verify-otp-forgotpassword", "/user/forgot-password", "/user/forgotpassword",
                                "/user/otp", "/user/verify-otp", "/user/login", "/user/register", "user/regForm",
                                "/css/**", "/js/**", "/images/**").permitAll()

                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()

                )

                .formLogin(form -> form

                        .loginPage("/user/login")
                        .loginProcessingUrl("/user/login")
                        .successHandler(success)

                        .failureUrl("/user/login?error=true")
                        .permitAll()

                )
                .logout(logout -> logout

                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/user/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()

                );

        return http.build();

    }

    @Bean
    PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

}
