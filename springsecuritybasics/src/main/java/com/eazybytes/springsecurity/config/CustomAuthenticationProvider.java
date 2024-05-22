package com.eazybytes.springsecurity.config;

import com.eazybytes.springsecurity.model.Authority;
import com.eazybytes.springsecurity.model.Customer;
import com.eazybytes.springsecurity.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * I have a question about the custom AuthenticationProvider. Is it correct that we do not make use of any UserDetailsManager / Service at all? We directly make use of the customer repository and authenticate the user by checking if the user with the given credentials exist in the db.
 * <p>
 * I am just confused because according to the whole internal flow it is shown that a AuthenticationProvider makes use of an UserDetailsManager/Service.
 * <p>
 * <p>
 * <p>
 * My other question is when we used the DaoAuthenticationProvider and had multiple implementations of UserDetailsService : Where exactly is the DaoAuthenticationProvider looking for the available implementations of UserDetailsService? I see that there is a setter for the UserDetailsService inside the DaoAuthenticationProvider but it there is no place where this method is being called.
 * <p>
 * <p>
 * <p>
 * I see that inside AbstractUserDetailsAuthenticationProvider which is a superclass of DaoAuthenticationProvider, the retrieveUser(..) method will invoke the loadUserByUserName(..) method of the given UserDetailsService.
 * <p>
 * <p>
 * <p>
 * I hope that you understand my issue and help me out here so I can understand it because I would like to have some clarification here before I can proceed with the course.
 * <p>
 * <p>
 * Answer is below
 *
 * <p>
 * With the UserDetailsService, we will fetch the user details and handover to the framework. Where as with AuthenticationProvider, we will fetch user details, populate the Authentication object and handover the same to framework. It doesn't matter if you are loading user details with Spring Data JPA method or UserDetailsService inside your AuthenticationProvider.  But I feel Spring Data JPA approach is simpler and clean.
 * <p>
 * The internal flow is to just show that default Authentication Providers like DaoAuthenticationProvider rely on the UserDetailsService. But in a custom Authentication Provider, it can be completely optional and we can directly use the Spring Data JPA code to fetch the user details from the DB.
 * <p>
 * Coming to the other question, based on the bean declaration inside the project, the correspondent UserDetailsService implementation will be used. We can't have multiple UserDetailsService implementations as it creates ambiguity.
 * <p>
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    public CustomerRepository customerRepository;
    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    public CustomUserDetails customUserDetails;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        String passwd = authentication.getCredentials().toString();

        /**
         * it is optional to load the userDetails from UserDetails interface or directly from  the
         * DB using Spring Data jpa as like belo commented code
         *
         * Suppose if we use to load the user details using the UserDetails interface implementation
         * then we would not be able to validate the below scenario
         *
         * And if I want to have a logic ex: person who greater than 18 old to allow login to app, we can't use UserDetailsService to perform that logic
         **/
        //we can also load the userDetails directly from DB without using UserDetails interface impl
        List<Customer> customers = customerRepository.findByEmail(userName);
        // UserDetails userDetails = customUserDetails.loadUserByUsername(userName);
        if (customers.size() > 0) {
            if (passwordEncoder.matches(passwd, customers.get(0).getPwd())) {
                List<GrantedAuthority> authorities = new ArrayList<>();
                //  grantedAuthorities.add(new SimpleGrantedAuthority(userDetails.getAuthorities()));

                return new UsernamePasswordAuthenticationToken(userName, passwd, getGrantedAuthority(customers.get(0).getAuthorities()));
            } else {
                throw new BadCredentialsException("invalid credentials!");
            }
        } else {
            throw new BadCredentialsException("user has not registered with us");
        }

    }


    private List<GrantedAuthority> getGrantedAuthority(Set<Authority> authorities) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Authority authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
        }
        return grantedAuthorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
