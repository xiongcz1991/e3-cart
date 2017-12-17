package cn.e3mall.search.message;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.search.mapper.SearchItemMapper;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ItemAddMessageListener implements MessageListener {
    @Autowired
    private SearchItemMapper searchItemMapper;
    @Autowired
    private SolrServer solrServer;
    @Override
    public void onMessage(Message message) {
        //从广播消息中取id
        try {
            TextMessage textMessage = (TextMessage) message;
            Long itemId = Long.parseLong(textMessage.getText());    //还可以new Long(textMessage.getText());
            //等待事务提交,防止事务还未提交就去数据库中查询该商品
            Thread.sleep(1000);
            //查询商品拼接类方便添加到索引
            SearchItem searchItem = this.searchItemMapper.getItemIndexById(itemId);
            //创建一个文档对象
            SolrInputDocument solrInputFields = new SolrInputDocument();
            //加入索引库
            solrInputFields.addField("id",searchItem.getId());
            solrInputFields.addField("item_title",searchItem.getTitle());
            solrInputFields.addField("item_sell_point",searchItem.getSell_point());
            solrInputFields.addField("item_price",searchItem.getPrice());
            solrInputFields.addField("item_image",searchItem.getImage());
            solrInputFields.addField("item_category_name",searchItem.getCategory_name());
            solrServer.add(solrInputFields);
            solrServer.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
