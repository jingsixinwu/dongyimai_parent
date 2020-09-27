package com.offcn.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.content.service.ContentService;
import com.offcn.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;
    /**
     * 根据分类查询广告信息
     */
    @RequestMapping("/findByCategoryId")
    public List<TbContent> findByCategoryId(Long categoryId){

          return contentService.findByCategoryId(categoryId);
     }


}
