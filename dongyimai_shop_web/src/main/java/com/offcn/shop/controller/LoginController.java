package com.offcn.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    //从 springsecurity中获取当前登陆的用户的信息
    @RequestMapping("/name")
    public Map name(){
        Map map=new HashMap();
      //从springsecurity中获取当前登陆用户的信息
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("loginName",name);

       return map;
    }
}
