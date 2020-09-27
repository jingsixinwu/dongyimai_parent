package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Result;
import com.offcn.group.Cart;
import com.offcn.util.CookieUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout=6000)
    private CartService cartService;

    /**
     * 购物车列表
     * @param request
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response){
        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("username:"+username);
        //从cookie中取数据
        //从cookie中获取购物车信息
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if(cartListString==null||cartListString==""){
            cartListString="[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
        if(username.equals("anonymousUser")){//未登录状态

             return cartList_cookie;



        }else{//登录状态

            List<Cart> cartList_redis =cartService.findCartListFromRedis(username);//从redis中提取
            //判断从cookie取出的购物车是否有数据
            if(cartList_cookie.size()>0){//有数据才合并

                 //合并购物车
                cartList_redis=cartService.mergeCartList(cartList_redis, cartList_cookie);
                //清除cookie中购物车的信息
                CookieUtil.deleteCookie(request, response, "cartList");

                //将合并后的数据存入redis
                cartService.saveCartListToRedis(username, cartList_redis);


            }




            return cartList_redis;

        }



    }

    /**
     * 添加SKU到 购物车中
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105")
   public Result addGoodsToCartList(HttpServletRequest request, HttpServletResponse response,Long itemId, Integer num){
       // response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");
       // response.setHeader("Access-Control-Allow-Credentials","true");

        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("username:"+username);
        try {
            List<Cart> oldcartList = findCartList(request,response);//获取购物车列表
            //将新的商品sku添加更新到购物车中
            List<Cart> newcartList = cartService.addGoodsToCartList(oldcartList,itemId, num);

            if(username.equals("anonymousUser")){//如果是未登录，保存到cookie
                //将新的购物车列表存入cookie中

                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(newcartList),3600*24,"UTF-8");

                System.out.println("未登录，将购物车数据保存到cookie中");


            }else{//如果登录保存到redis中

                cartService.saveCartListToRedis(username, newcartList);
                System.out.println("已经登录，将购物车数据保存到redis中");

            }





            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }

    }




}
