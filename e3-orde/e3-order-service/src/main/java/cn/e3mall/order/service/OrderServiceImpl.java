package cn.e3mall.order.service;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.mapper.TbOrderItemMapper;
import cn.e3mall.mapper.TbOrderMapper;
import cn.e3mall.mapper.TbOrderShippingMapper;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbOrder;
import cn.e3mall.pojo.TbOrderItem;
import cn.e3mall.pojo.TbOrderShipping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    private JedisClient jedisClient;
    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbOrderShippingMapper orderShippingMapper;
    @Autowired
    private TbItemMapper itemMapper;

    @Value("${ORDER_ID_GEN_KEY}")
    private String ORDER_ID_GEN_KEY;//ORDER_ID_GEN_KEY  订单号key
    @Value("${ORDER_ID_START}")
    private String ORDER_ID_START;//100544 起始id
    @Value("${ORDER_DETAIL_ID_GEN_KEY}")
    private String ORDER_DETAIL_ID_GEN_KEY; //ORDER_DETAIL_ID_GEN 购买商品明细号key
    //生成订单
    @Override
    public E3Result createOrder(OrderInfo orderInfo) {
        //身材订单号,使用redis的incr生成.
        if (!jedisClient.exists(ORDER_ID_GEN_KEY)){
            jedisClient.set(ORDER_ID_GEN_KEY,ORDER_ID_START);
        }
        String orderId = jedisClient.incr(ORDER_ID_GEN_KEY).toString();
        //补全orderInfo属性
        orderInfo.setOrderId(orderId);
        //1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
        orderInfo.setStatus(1);
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        //插入订单表
        orderMapper.insert(orderInfo);
        //想订单明细插入数据
        List<TbOrderItem> orderItems = orderInfo.getOrderList();
        for (TbOrderItem orderItem : orderItems) {
            //生成明细id
            String odId = jedisClient.incr(ORDER_DETAIL_ID_GEN_KEY).toString();
            orderItem.setOrderId(orderId);
            orderItem.setId(odId);
            //向明细表插入数据
            orderItemMapper.insert(orderItem);
            //商品表商品数量修改
            Integer num = orderItem.getNum();
            TbItem item = this.itemMapper.selectByPrimaryKey(Long.parseLong(orderItem.getItemId()));
            Integer total = item.getNum();
            if (total-num<0) {
               return E3Result.build(404,"商品已卖光!");
            }
            item.setNum(total - num);
            this.itemMapper.updateByPrimaryKey(item);
        }
        //向订单物流表插入数据
        TbOrderShipping tbOrderShipping = orderInfo.getOrderShipping();
        tbOrderShipping.setCreated(new Date());
        tbOrderShipping.setCreated(new Date());
        tbOrderShipping.setOrderId(orderId);
        this.orderShippingMapper.insert(tbOrderShipping);
        //返回E3Result, 包含订单号
        return E3Result.ok(orderId);
    }
}
