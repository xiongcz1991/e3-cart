package cn.e3mall.controller;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
public class ContentController {
    @Autowired
    private ContentService contentService;
    //添加内容
    @RequestMapping(value = "/content/save",method = RequestMethod.POST)
    @ResponseBody
    public E3Result addContent(TbContent tbContent){
        return this.contentService.addContent(tbContent);
    }
    //内容分页
    @RequestMapping("/content/query/list")
    @ResponseBody
    public EasyUIDataGridResult getContentList(Long categoryId, int page , int rows){
       return this.contentService.getContentList(categoryId,page,rows);
    }
    //修改完成 保存
    @RequestMapping(value = "/rest/content/edit",method = RequestMethod.POST)
    @ResponseBody
    public E3Result saveContent(TbContent content){
        return this.contentService.saveContent(content);
    }
    //删除
    @RequestMapping("/content/delete")
    @ResponseBody
    public E3Result deleteContent(String ids,Long categoryId){
        return this.contentService.deleteContent(ids,categoryId);
    }
}
