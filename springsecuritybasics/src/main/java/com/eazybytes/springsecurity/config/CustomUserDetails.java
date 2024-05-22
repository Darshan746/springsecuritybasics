package com.eazybytes.springsecurity.config;

import com.eazybytes.springsecurity.model.Customer;
import com.eazybytes.springsecurity.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetails implements UserDetailsService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       String userName , password;
       List<GrantedAuthority> grantedAuthorities = null;
       List<Customer> customers =  customerRepository.findByEmail(username);
       if (customers.isEmpty()){
           throw new UsernameNotFoundException("user details not found for the user :"+username);
       }else {
            userName = customers.get(0).getEmail();
            password = customers.get(0).getPwd();
            grantedAuthorities = new ArrayList<>();
            grantedAuthorities.add(new SimpleGrantedAuthority(customers.get(0).getRole()));
       }
        return new User(username, password, grantedAuthorities);
    }
}
