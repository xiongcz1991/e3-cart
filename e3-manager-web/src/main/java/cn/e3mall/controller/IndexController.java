package cn.e3mall.controller;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.search.service.SearchItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {
    @Autowired
    private SearchItemService searchItemService;
    //生成索引库
    @RequestMapping("/index/item/import")
    @ResponseBody
    public E3Result getIndexList(){
        return this.searchItemService.getIndexList();
    }
}
