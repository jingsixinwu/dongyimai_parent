package com.offcn.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.cart.service.CartService;
import com.offcn.group.Cart;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据商品SKU ID查询SKU商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);

        //判断是否为空
        if(item==null){

            throw new RuntimeException("商品不存在");
        }

        if(!item.getStatus().equals("1")){
            throw new RuntimeException("商品状态无效");
        }

        //2.获取商家ID
        String sellerId = item.getSellerId();
        //3.根据商家ID判断购物车列表中是否存在该商家的购物车
        Cart cart=searchCartBySellerId(cartList,sellerId);
        if(cart==null) {//购物车不存在
            //4.如果购物车列表中不存在该商家的购物车
            //4.1 新建购物车对象
            cart=new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            TbOrderItem orderItem = createOrderItem(item,num);
            List<TbOrderItem> orderItemsList = new ArrayList<TbOrderItem>();
            orderItemsList.add(orderItem);
            cart.setOrderItemList(orderItemsList);
            //4.2 将新建的购物车对象添加到购物车列表
            cartList.add(cart);

        }else{
            //5.如果购物车列表中存在该商家的购物车
            // 查询购物车明细列表中是否存在该商品
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(),itemId);
            if(orderItem==null){
                //5.1. 如果没有，新增购物车明细
                 orderItem= createOrderItem(item, num);
                 cart.getOrderItemList().add(orderItem);

            }else{
                //5.2. 如果有，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue())  );
                //判断订单明细中商品的数量
                if(orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);//移除购物车明细
                }
               //如果移除后cart的明细数量为0，则将cart移除
                if(cart.getOrderItemList().size()==0){
                    cartList.remove(cart);
                }
            }

        }
       return cartList;
    }


    /**
     * 根据商家的编号查询购物车
     */
    private Cart searchCartBySellerId(List<Cart> cartList,String sellerId){
        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
            
        }
        return null;
     }

    /**
     * 根据sku num 创建订单明细
     */
    private TbOrderItem createOrderItem(TbItem item,Integer num){

          if(num<=0){
              throw new RuntimeException("数量非法");
          }

        TbOrderItem orderItem = new TbOrderItem();

          orderItem.setItemId(item.getId());
          orderItem.setSellerId(item.getSellerId());
          orderItem.setNum(num);
          orderItem.setPrice(item.getPrice());
          orderItem.setTitle(item.getTitle());
          orderItem.setPicPath(item.getImage());
          orderItem.setGoodsId(item.getGoodsId());
          orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));

          return orderItem;
    }

    /**
     * 根据商品id 到购物车订单明细列表中查询是否有个该商品订单明细
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem>orderItemList,Long itemId){
        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId().longValue()==itemId.longValue()){

                return orderItem;
            }

        }
        return null;


    }

    /**
     * 从redis中获取购物车信息
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中取出购物车的信息:"+username);
        List<Cart> cartList=(List<Cart>)redisTemplate.boundHashOps("cartList").get(username);

        if(cartList==null){

            cartList=new ArrayList<Cart>();
        }


        return cartList;
    }

    /**
     * 将购物车保存到redis中gou
     * @param username
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("保存购物车信息到redis中:"+username);
        redisTemplate.boundHashOps("cartList").put(username,cartList);

    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        System.out.println("合并购物车");

        for (Cart cart : cartList2) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                 cartList1=addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());

            }
        }

        return cartList1;
    }
}
