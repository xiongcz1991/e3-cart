package cn.e3mall.cart.service;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.pojo.TbItem;

import java.util.List;

public interface CartService {

    E3Result addCart(Long id, Long itemId, Integer num);

    E3Result mergeCart(Long id, List<TbItem> cartList);

    List<TbItem> getCartList(Long id);

    E3Result updateCartNum(Long id, Long itemId, Integer num);

    E3Result deleteCartItem(Long id, Long itemId);

    E3Result clearCartList(Long id);
}
