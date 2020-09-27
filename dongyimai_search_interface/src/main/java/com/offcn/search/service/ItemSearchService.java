package com.offcn.search.service;

import com.offcn.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 根据查询关键字 查询索引库中商品sku信息
     */

    public Map<String, Object> search(Map searchMap);

    /**
     * 导入数据
     */
    public void importList(List<TbItem> list);
    /**
     * 删除对应索引库数据
     *
     */
    public void deleteByGoodsIds(Long[] goodsIds);

}
