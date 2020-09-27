package com.offcn.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/itemSearch")
public class ItemSearchController {
    @Reference
    private ItemSearchService itemSearchService;

    /**
     * 根据查询条件查询索引库中商品的信息
     */
    @RequestMapping("/search")
    public Map<String, Object> search(@RequestBody Map searchMap){
        System.out.println("searchMap:"+searchMap);

        return itemSearchService.search(searchMap);

    }


}
