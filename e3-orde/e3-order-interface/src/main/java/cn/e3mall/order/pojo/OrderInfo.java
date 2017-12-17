package cn.e3mall.order.pojo;

import cn.e3mall.pojo.TbOrder;
import cn.e3mall.pojo.TbOrderItem;
import cn.e3mall.pojo.TbOrderShipping;

import java.io.Serializable;
import java.util.List;

public class OrderInfo extends TbOrder implements Serializable{
    private List<TbOrderItem> orderList;
    private TbOrderShipping orderShipping;

    public List<TbOrderItem> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<TbOrderItem> orderList) {
        this.orderList = orderList;
    }

    public TbOrderShipping getOrderShipping() {
        return orderShipping;
    }

    public void setOrderShipping(TbOrderShipping orderShipping) {
        this.orderShipping = orderShipping;
    }
}
