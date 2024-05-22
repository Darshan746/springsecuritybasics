package com.eazybytes.springsecurity.config;

import com.eazybytes.springsecurity.filter.JWTTokenGeneratorFilter;
import com.eazybytes.springsecurity.filter.JWTTokenValidatorFilter;
import com.eazybytes.springsecurity.filter.RequestValidationBeforeFilter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class ProjectSecurityConfig {


    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests((requests) -> requests.requestMatchers("/myBalance", "/myAccount", "/myCards").hasRole("ADMIN"));
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class);
        http.addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class);
        http.addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class);
        http.authorizeHttpRequests((requests) -> requests.requestMatchers("/contact", "/notices", "/register", "/error", "/user").permitAll());
        http.httpBasic(withDefaults());
        return http.build();
    }


    /**
     * the below one is a type of userDetail Manager it tells the springSecurity that
     * this fellow wants to use in memory style of authentication
     */

    /*@Bean
    public InMemoryUserDetailsManager defaultInMemoryUserDetails() {
        UserDetails admin = User.withDefaultPasswordEncoder().username("darshan")
                .password("darshan").
                authorities("admin").
                build();
        UserDetails user = User.withDefaultPasswordEncoder().username("hemanth")
                .password("hemanth").
                authorities("read").
                build();
        return new InMemoryUserDetailsManager(admin, user);
    }*/


   /* @Bean
    public UserDetailsService userDetailsService(DataSource dataSource){
        return new JdbcUserDetailsManager(dataSource);
    }*/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
