package com.offcn.cart.service;

import com.offcn.group.Cart;

import java.util.List;

/**
 * 购物车服务接口
 * @author Administrator
 *
 */
public interface CartService {

    /**
     * 商品信息添加到购物车
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num );

    /**
     * 从redis中获取购物车列表
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 将购物车信息保存到redis中
     */
    public void saveCartListToRedis(String username,List<Cart> cartList);

    /**
     * 合并购物车
     */
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);



}
