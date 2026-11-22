package com.scm.configurations;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@Configuration
public class SecurityConfig {
    private AuthFailureHandler authFailureHandler;

    //oauth2
    private OAuthenticationSuccessHandler handler;

    public SecurityConfig(AuthFailureHandler authFailureHandler, OAuthenticationSuccessHandler handler) {
        this.authFailureHandler = authFailureHandler;
        this.handler = handler;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //Configuration of AuthenticationProvider
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder){


        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);

        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        return daoAuthenticationProvider;
    }
    //Configure which made need security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationProvider authenticationProvider)throws Exception{
        //configuration
        httpSecurity.authenticationProvider(authenticationProvider);
        httpSecurity.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers(
                            "/",
                            "/home",
                            "/about",
                            "/services",
                            "/contact",
                            "/signup",
                            "/auth/**",
                            "/do-signup",
                            "/forgot-password",
                            "/reset-password",
                            "/login",
                            "/error",
                            "/css/**",
                            "/js/**",
                            "/image/**"
                    ).permitAll()

                    .requestMatchers("/user/**").authenticated()
                    .anyRequest().authenticated();
        });
        httpSecurity.formLogin(form ->{
            form.loginPage("/login");
            form.loginProcessingUrl("/authenticate");
            form.defaultSuccessUrl("/user/dashboard",true);
            form.failureUrl("/login?error=true");
            form.usernameParameter("email");
            form.passwordParameter("password");

            form.failureHandler(authFailureHandler);
        });


        //httpSecurity.formLogin(Customizer.withDefaults());
    //for logout we need to disable csrf protection
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.logout(log ->{
            log.logoutUrl("/logout");
            log.logoutSuccessUrl("/login?logout=true");
            log.invalidateHttpSession(true);
            log.clearAuthentication(true);
            log.permitAll();
        });


        //oauth2 configuration
        httpSecurity.oauth2Login(oauth ->{
            oauth.loginPage("/login");
            oauth.successHandler(handler);
        });

        return httpSecurity.build();
    }

}
