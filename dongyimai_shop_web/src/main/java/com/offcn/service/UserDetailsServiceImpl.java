package com.offcn.service;

import com.offcn.pojo.TbSeller;
import com.offcn.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

/**
 * 自定义的认证管理类
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;
    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("进入了自定义的认证类============================================="+username);
         //构建角色
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //查询所有的商家

        TbSeller seller = sellerService.findOne(username);

        System.out.println("seller:"+seller);

        if(seller!=null){// 查到商家
            if(seller.getStatus().equals("1")){//商家是审核通过的
                return new User(username,seller.getPassword(),grantedAuthorities);
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
}
