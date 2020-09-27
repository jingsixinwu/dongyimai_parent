package com.offcn.order.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


public class UserDetailsServiceImpl implements UserDetailsService {
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //构建角色集合
        List<GrantedAuthority> authorities=new ArrayList();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        System.out.println("==================username:"+username);
        return new User(username,"",authorities);
    }
}
