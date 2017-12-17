package cn.e3mall.order.controller;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

//订单管理
@Controller
public class OrderController {
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;

    @RequestMapping("/order/order-cart")
    public String showOrderCart(HttpServletRequest request){
        TbUser user = (TbUser)request.getAttribute("user");
        List<TbItem> cartList = cartService.getCartList(user.getId());
        request.setAttribute("cartList",cartList);
        //返回页面
        return "order-cart";
    }

    //生成订单
    @RequestMapping(value = "/order/create",method = RequestMethod.POST)
    public String createOrder(OrderInfo orderInfo ,HttpServletRequest request){
        //取用户信息
        TbUser user = (TbUser) request.getAttribute("user");
        //将用户信息添加到OrderInfo中
        orderInfo.setUserId(user.getId());
        orderInfo.setBuyerNick(user.getUsername());
        //调用服务生成订单
        E3Result result = orderService.createOrder(orderInfo);
        //如果生成订单成功,需要删除购物车
        if (result.getStatus()==200){
            //清空购物车
            cartService.clearCartList(user.getId());
        }
        //将订单号传递给页面
        request.setAttribute("orderId",result.getData());
        request.setAttribute("payment",orderInfo.getPayment());
        //返回逻辑视图
        return "success";
    }
}
