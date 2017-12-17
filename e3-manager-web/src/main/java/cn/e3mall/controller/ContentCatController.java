package cn.e3mall.controller;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.content.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ContentCatController {
    @Autowired
    private ContentCategoryService contentCategoryService;
    @RequestMapping("/content/category/list")
    @ResponseBody
    public List<EasyUITreeNode> getContentCatList(@RequestParam(name = "id",defaultValue = "0") Long parentId){
        return  this.contentCategoryService.getContentCatList(parentId);
    }
    //添加分类节点
    @RequestMapping(value = "/content/category/create",method = RequestMethod.POST)
    @ResponseBody
    public E3Result createContentCategory(Long parentId ,String name){
        return this.contentCategoryService.addContentCategory(parentId, name);
    }
    //删除
    @RequestMapping("/content/category/delete")
    @ResponseBody
    public E3Result deleteContentCategory(Long id){
        return this.contentCategoryService.deleteContentCategory(id);
    }
    //修改
    @RequestMapping("/content/category/update")
    @ResponseBody
    public E3Result updateContentCategory(Long id,String name){
        return this.contentCategoryService.updateContentCategory(id,name);
    }
}

