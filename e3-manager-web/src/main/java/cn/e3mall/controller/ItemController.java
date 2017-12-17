package cn.e3mall.controller;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemCat;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;
    //处理分页
    @RequestMapping("/item/list")
    @ResponseBody
    public EasyUIDataGridResult getItemList(int page ,int rows){
        EasyUIDataGridResult result= this.itemService.getItemList(page,rows);
        return result;
    }
    //添加商品,和描述
    @RequestMapping(value = "/item/save",method = RequestMethod.POST)
    @ResponseBody
    public E3Result addItem(TbItem item,String desc){
        E3Result e3Result = this.itemService.addItem(item, desc);
        System.out.println(e3Result);
        return e3Result;
    }
    //删除商品和描述
    @RequestMapping("/rest/item/delete")
    @ResponseBody
    public E3Result deleteItem(String ids){
        E3Result result = this.itemService.deleteItem(ids);
        return result;
    }
    //下架商品
    @RequestMapping("/rest/item/instock")
    @ResponseBody
    public E3Result instockItem(String ids){
        E3Result result = this.itemService.instockItem(ids);
        return result;
    }
    //上架商品
    @RequestMapping("/rest/item/reshelf")
    @ResponseBody
    public E3Result reshelfItem(String ids){
        E3Result result = this.itemService.reshelfItem(ids);
        return result;
    }
    //修改商品
    @RequestMapping(value = "/rest/item/update",method = RequestMethod.POST)
    @ResponseBody
    public E3Result updateItem(TbItem item,String desc){
        return this.itemService.updateItem(item,desc);
    }
    //加载商品描述
    @RequestMapping("/rest/item/query/item/desc/{id}")
    @ResponseBody
    public TbItemDesc editDesc(@PathVariable String id){
       return this.itemService.getItemDescById(id);
    }
    //加载商品规格
}
