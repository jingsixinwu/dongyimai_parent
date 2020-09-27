package com.offcn.content.controller;

import com.offcn.entity.Result;
import com.offcn.util.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件的上传
 */
@RestController
public class UploadController {
   @Value("${FILE_SERVER_URL}")
   private String FILE_SERVER_URL;
   @RequestMapping("/upload")
   public Result upload(MultipartFile file){
       //获取文件的原始名称
       String originalFilename = file.getOriginalFilename();
       //取出上传文件的扩展名
       String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
       //2、创建一个 FastDFS 的客户端
       try {
           FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
           //3、执行上传处理
           String path = fastDFSClient.uploadFile(file.getBytes(), extName);
          //4、拼接返回的 url 和 ip 地址，拼装成完整的 url
           String url=FILE_SERVER_URL+path;
           return new Result(true,url);


       } catch (Exception e) {
           e.printStackTrace();
           return new Result(false,"上传失败");
       }

   }




}
