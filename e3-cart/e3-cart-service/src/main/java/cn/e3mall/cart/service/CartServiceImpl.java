package cn.e3mall.cart.service;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private JedisClient jedisClient;
    @Autowired
    private TbItemMapper itemMapper;
    @Value("REDIS_CART_PRE")
    private String REDIS_CART_PRE;  //CART

    @Override
    public E3Result addCart(Long id, Long itemId, Integer num) {
        //向redis中添加购物车。
        //数据类型是hash key：用户id field：商品id value：商品信息
        //判断商品是否存在
        Boolean hexists = jedisClient.hexists(REDIS_CART_PRE + ":" + id, itemId + "");
        if (hexists){
            String json = jedisClient.hget(REDIS_CART_PRE + ":" + id, itemId + "");
            TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
            tbItem.setNum(tbItem.getNum()+num);
            //写回redis
            jedisClient.hset(REDIS_CART_PRE + ":" + id, itemId + "",JsonUtils.objectToJson(tbItem));
            return E3Result.ok();
        }
        //如果不存在.更具商品id取商品信息
        TbItem tbItem = this.itemMapper.selectByPrimaryKey(itemId);
        //设置购物车数据量
        tbItem.setNum(num);
        //取一张图片
        if (StringUtils.isNotBlank(tbItem.getImage())) {
            tbItem.setImage(tbItem.getImage().split(",")[0]);
        }
        //添加到购物车列表
        jedisClient.hset(REDIS_CART_PRE + ":" + id, itemId + "",JsonUtils.objectToJson(tbItem));
        return E3Result.ok();
    }
    //合并购物车
    @Override
    public E3Result mergeCart(Long id, List<TbItem> cartList) {
        //遍历商品列表
        //把列表添加到购物车。
        //判断购物车中是否有此商品
        //如果有，数量相加
        //如果没有添加新的商品
        for (TbItem tbItem : cartList) {
            this.addCart(id,tbItem.getId(),tbItem.getNum());
        }
        return E3Result.ok();
    }
    //获取用户的购物车
    @Override
    public List<TbItem> getCartList(Long id) {
        List<String> hvals = jedisClient.hvals(REDIS_CART_PRE + ":" + id);
        ArrayList<TbItem> tbItems = new ArrayList<>();
        for (String item : hvals) {
            //创建一个TbItem对象
            TbItem tbItem = JsonUtils.jsonToPojo(item, TbItem.class);
            //添加到列表
            tbItems.add(tbItem);
        }
        return tbItems;
    }

    @Override
    public E3Result updateCartNum(Long id, Long itemId, Integer num) {
        String json = jedisClient.hget(REDIS_CART_PRE + ":" + id, itemId + "");
        TbItem tbItem = JsonUtils.jsonToPojo(json,TbItem.class);
        tbItem.setNum(num);
        jedisClient.hset(REDIS_CART_PRE + ":" + id, itemId + "",JsonUtils.objectToJson(tbItem));
        return E3Result.ok();
    }

    @Override
    public E3Result deleteCartItem(Long id, Long itemId) {
        // 删除购物车商品
        jedisClient.hdel(REDIS_CART_PRE + ":" + id, itemId + "");
        return E3Result.ok();
    }

    @Override
    public E3Result clearCartList(Long id) {
        //删除购物车
        jedisClient.hdel(REDIS_CART_PRE + ":" + id);
        return E3Result.ok();
    }
}
