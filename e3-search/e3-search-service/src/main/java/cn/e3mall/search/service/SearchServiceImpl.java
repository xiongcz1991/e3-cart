package cn.e3mall.search.service;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.dao.SearchDao;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SearchDao searchDao;
    @Override
    public SearchResult searchItemList(String keyword, int page, int rows)throws Exception{
        SolrQuery query = new SolrQuery();
        query.setQuery(keyword);
        if (page<=0)page = 1;
        //设置起始行
        query.setStart((page-1)*rows);
        //设置每页行数
        query.setRows(rows);
        //设置默认搜索域
        query.set("df","item_title");
        //开启高亮
        query.setHighlight(true);
        //添加高亮查询的域
        query.addHighlightField("item_title");
        query.setHighlightSimplePre("<em style=\"color:red\">");
        query.setHighlightSimplePost("</em>");
        //使用dao查询
         SearchResult searchResult = this.searchDao.searchItemList(query);
        //计算总页数
        searchResult.setTotalPage((int) ((searchResult.getRecordCount()+rows-1)/rows));
        return searchResult;
    }
}
