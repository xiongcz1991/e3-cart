package cn.e3mall.itemdisplay.controller;

import cn.e3mall.itemdisplay.pojo.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ItemdisplayController {
    @Autowired
    private ItemService itemService;
    //freemarker方式访问商品静态页面
//    @RequestMapping("/item/{itemId}")
//    private String showItemInfo(@PathVariable Long itemId){
//        return itemId+"";
//    }
    //redis缓存方式访问商品详情页面
    @RequestMapping("/item/{itemId}")
    public String showItemInfo(@PathVariable Long itemId, Model model) {
        //调用服务取商品基本信息
        TbItem tbItem = itemService.getItemById(itemId);
        Item item = new Item(tbItem);
        //取商品描述信息
        TbItemDesc itemDesc = itemService.getItemDescById(itemId+"");
        //把信息传递给页面
        model.addAttribute("item", item);
        model.addAttribute("itemDesc", itemDesc);
        //返回逻辑视图
        return "item";
    }
}
