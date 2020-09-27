package com.offcn.page.service;

/**
 * 商品详情页的接口
 */
public interface ItemPageService {

    /**
     * 生成商品详细页
     * @param goodsId
     */
    public boolean genItemHtml(Long goodsId);

    /**
     * 删除商品详情页
     */
    public boolean deleteItemHtml(Long[]goodsIds);
}
