package cn.e3mall.controller;

import cn.e3mall.common.utils.FastDFSClient;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.service.ItemService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

@Controller
public class PictureController {

    @Value("${IMAGE_SERVER_URL}")
    private String IMAGE_SERVER_URL;

    @RequestMapping(value="/pic/upload", produces= MediaType.TEXT_PLAIN_VALUE+";charset=utf-8")
    @ResponseBody
    public String uploadFile(MultipartFile uploadFile){
        try {
            //把图片上传的图片服务器
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf/client.conf");
            //获取文件扩展名
            String originalFilename = uploadFile.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            //得到一个图片的地址和文件名
            String url = fastDFSClient.uploadFile(uploadFile.getBytes(), extension);
            //补充为完整的url
            url = IMAGE_SERVER_URL + url;
            HashMap result = new HashMap<>();
            result.put("error",0);
            result.put("url",url);
            String json = JsonUtils.objectToJson(result);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            HashMap result = new HashMap<>();
            result.put("error",1);
            result.put("message","图片上传失败");
            String json = JsonUtils.objectToJson(result);
            return json;
        }
    }
}
