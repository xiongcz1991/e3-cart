package cn.e3mall.search.service;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.search.mapper.SearchItemMapper;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchItemServiceImpl implements SearchItemService {
    @Autowired
    private SearchItemMapper searchItemMapper;
    @Autowired
    private SolrServer solrServer;
    //查询所有
    @Override
    public E3Result getIndexList() {
        try {
            List<SearchItem> indexList = this.searchItemMapper.getIndexList();
            for (SearchItem searchItem : indexList) {
                SolrInputDocument solrInputFields = new SolrInputDocument();
                //向文档中添加域
                solrInputFields.addField("id",searchItem.getId());
                solrInputFields.addField("item_title",searchItem.getTitle());
                solrInputFields.addField("item_sell_point",searchItem.getSell_point());
                solrInputFields.addField("item_price",searchItem.getPrice());
                solrInputFields.addField("item_image",searchItem.getImage());
                solrInputFields.addField("item_category_name",searchItem.getCategory_name());
                //将域写入索引中
                this.solrServer.add(solrInputFields);
            }
            //提交
            solrServer.commit();
            return E3Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return E3Result.build(500,"数据导入异常!");
        }

    }
}
