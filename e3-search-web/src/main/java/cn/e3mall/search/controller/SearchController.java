package cn.e3mall.search.controller;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;

@Controller
public class SearchController {
    @Value("${SEARCH_RESULT_ROWS}")
    private Integer SEARCH_RESULT_ROWS;
    @Autowired
    private SearchService searchService;
    @RequestMapping("/search")
    public String searchItemList(String keyword, @RequestParam(defaultValue = "1") Integer page, Model model) throws Exception {
//        //测试全局错误页面
//        int i = 1/0;
        //解决查询条件乱码
        keyword = new String(keyword.getBytes("iso-8859-1"),"utf-8");
        SearchResult searchResult = this.searchService.searchItemList(keyword, page, this.SEARCH_RESULT_ROWS);
        model.addAttribute("totalPages",searchResult.getTotalPage());
        model.addAttribute("query",keyword);    //回显查询条件
        model.addAttribute("recourdCount",searchResult.getRecordCount());
        model.addAttribute("itemList",searchResult.getSearchItems());
        return "search";
    }
}
