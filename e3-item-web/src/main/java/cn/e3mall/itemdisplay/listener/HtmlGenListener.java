package cn.e3mall.itemdisplay.listener;

import cn.e3mall.itemdisplay.pojo.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class HtmlGenListener implements MessageListener {
    @Value("${HTML_GEN_PATH}")
    private String HTML_GEN_PATH;
    @Autowired
    private ItemService itemService;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Override
    public void onMessage(Message message) {
        try {
            //从消息中取出id
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            Long itemId = new Long(text);
            //等待manage中添加商品事务提交
            Thread.sleep(1000);
            //根据商品id查询商品
            TbItem tbItem = this.itemService.getItemById(itemId);
            Item item = new Item(tbItem);
            //取商品描述
            TbItemDesc desc = this.itemService.getItemDescById(itemId + "");
            //创建数据集封装数据
            Map data = new HashMap<>();
            data.put("item",item);
            data.put("itemDec",desc);
            //加载模板对象
            Configuration configuration = freeMarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            //创建一个输出流,指定输出的目录及文件名
            FileWriter writer = new FileWriter(HTML_GEN_PATH + itemId + ".html");
            //生成静态页面
            template.process(data,writer);
            //关闭流
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
