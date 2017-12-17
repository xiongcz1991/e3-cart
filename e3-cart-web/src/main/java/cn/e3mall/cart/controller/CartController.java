package cn.e3mall.cart.controller;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {
    @Autowired
    private ItemService itemService;
    @Autowired
    private CartService cartService;
    @Value("${COOKIE_CART_EXPIRE}")
    private Integer COOKIE_CART_EXPIRE; //设置cookie过期时间 5天
    @RequestMapping("/cart/add/{itemId}")
    public String addCart(@PathVariable Long itemId , @RequestParam(defaultValue = "1") Integer num,
     HttpServletRequest request, HttpServletResponse response){
       //判断用户是否登录
        TbUser user = (TbUser) request.getAttribute("user");
        //若登录,将购物车写入redis
        if (user!=null){
            this.cartService.addCart(user.getId(),itemId,num);
            return "cartSuccess";
        }
        //未登录使用cookie
        //从cookie中取购物车列表
        List<TbItem> items = this.getCartListFromCookie(request);
        //判断商品在商品列表
        boolean flag = false;
        for (TbItem tbItem : items){
            //如果商品已存在  则数量相加
            if (tbItem.getId() == itemId.longValue()){
                flag = true;
                //找到商品,数量相加 tbItem.getNum()购物车中原来存在的数量
                tbItem.setNum(tbItem.getNum()+num);
                break;  //跳出循环
            }
        }
        //如果不存在
        if(!flag){
            //根据商品id查询商品信息,
            TbItem tbItem = this.itemService.getItemById(itemId);
            //设置商品数量
            tbItem.setNum(num);
            //取其中第一张图片
            String image = tbItem.getImage();
            if (StringUtils.isNotBlank(image)){
                tbItem.setImage(image.split(",")[0]);
            }
            items.add(tbItem);
        }
        //写入cookie
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(items), COOKIE_CART_EXPIRE, true);
        //返回添加成功页面
        return "cartSuccess";
    }
    //公用:从cookie中取购物车列表方法
    private List<TbItem> getCartListFromCookie(HttpServletRequest request){
        String json = CookieUtils.getCookieValue(request, "cart", true);//true表示要解码
        //判断json是否为空
        if (StringUtils.isBlank(json)){
            return new ArrayList<>();
        }
        //把json转换为商品列表
        List<TbItem> list = JsonUtils.jsonToList(json,TbItem.class);
        return list;
    }

    //展示购物车列表
    @RequestMapping("/cart/cart")
    public String showCartList(HttpServletRequest request, HttpServletResponse response){
        //从cookie中取购物车列表
        List<TbItem> cartList = getCartListFromCookie(request);
        //判断用户是否登录
        TbUser user = (TbUser) request.getAttribute("user");
        //若是
        if (user!=null){
            //从cookie中取出购物车列表
            //如果不为空，把cookie中的购物车商品和服务端的购物车商品合并。
            this.cartService.mergeCart(user.getId(),cartList);
            //将cookie中的购物车删除
            CookieUtils.deleteCookie(request,response,"cart");
            //从服务端取购物车列表
            cartList = cartService.getCartList(user.getId());
        }
        //将列表传给页面
        request.setAttribute("cartList",cartList);
        //返回逻辑视图
        return "cart";
    }

    //更新购物车商品数量
    @RequestMapping("/cart/update/num/{itemId}/{num}")
    @ResponseBody
    public E3Result updateCartNum(@PathVariable Long itemId, @PathVariable Integer num
            , HttpServletRequest request ,HttpServletResponse response){
        //判断用户是否为登录状态
        TbUser user = (TbUser) request.getAttribute("user");
        if (user!=null){
            this.cartService.updateCartNum(user.getId(),itemId,num);
            return E3Result.ok();
        }
        //从cookie中取购物车列表
        List<TbItem> cartList = getCartListFromCookie(request);
        for (TbItem tbItem : cartList) {
            if (tbItem.getId()==itemId.longValue()){
                //更新数量
                tbItem.setNum(num);
                break;
            }
        }
        //将购物车列表写回cookie
        CookieUtils.setCookie(request,response,"cart",
                JsonUtils.objectToJson(cartList),COOKIE_CART_EXPIRE,true);
        return E3Result.ok();
    }

    //删除购物车商品
    @RequestMapping("/cart/delete/{itemId}")
    public String deleteCartItem(@PathVariable Long itemId ,HttpServletRequest request,
    HttpServletResponse response){
        //判断用户登录状态
        TbUser user = (TbUser) request.getAttribute("user");
        if (user!=null){
            this.cartService.deleteCartItem(user.getId(),itemId);
            return "redirect:/cart/cart.html";  //斜杠开始.表示工程或localhost之后
        }
        //从cookie中取购物车列表
        List<TbItem> cartList = getCartListFromCookie(request);
        //遍历.找到要删除的商品
        for (TbItem tbItem : cartList) {
           if (tbItem.getId()==itemId.longValue()){
               //删除商品
               cartList.remove(tbItem);
               break;
           }
        }
        //把购物车列表写入cookie
        CookieUtils.setCookie(request, response, "cart", JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIRE, true);
        //返回逻辑视图
        return "redirect:/cart/cart.html";
    }
}
