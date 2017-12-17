package cn.e3mall.search.dao;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class SearchDao {
    @Autowired
    private SolrServer solrServer;

    public SearchResult searchItemList(SolrQuery query) throws Exception {
        //根据solrQuery查询索引库
        QueryResponse queryResponse = solrServer.query(query);
        //取查询结果     ArrayList的子类
        SolrDocumentList results = queryResponse.getResults();
        //取查询结果总结构数
        long numFound = results.getNumFound();
        //set
        SearchResult searchResult = new SearchResult();
        searchResult.setRecordCount(numFound);
        //取商品列表,需要高亮显示
        Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
        List<SearchItem> list = new ArrayList<>();
        for (SolrDocument document : results) {
            SearchItem searchItem = new SearchItem();
            searchItem.setId((String) document.get("id"));
            //searchItem.setTitle((String) document.get("item_title"));
            searchItem.setSell_point((String) document.get("item_sell_point"));
            searchItem.setPrice((Long) document.get("item_price"));
            searchItem.setImage((String) document.get("item_image"));
            searchItem.setCategory_name((String) document.get("item_category_name"));
            //取高亮显示
            List<String> highList = highlighting.get(document.get("id")).get("item_title");
            String title = "";
            if (list!=null&&list.size()>0){
                title = highList.get(0);
            }else {
                title = (String) document.get("item_title");
            }
            searchItem.setTitle(title);
            list.add(searchItem);
        }
        //set
        searchResult.setSearchItems(list);
        return searchResult;
    }
}
