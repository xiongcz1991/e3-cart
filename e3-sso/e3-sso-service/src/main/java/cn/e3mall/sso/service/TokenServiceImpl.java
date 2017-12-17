package cn.e3mall.sso.service;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService{
    @Autowired
    private JedisClient jedisClient;
    @Value("${SESSION_EXPIRE}")
    private Integer SESSION_EXPIRE;
    //验证用户是否已登录
    @Override
    public E3Result getUserByToken(String token) {
        String json = jedisClient.get("SESSION:"+token);
        if (StringUtils.isBlank(json)){
            return E3Result.build(201,"用户登录已过期");
        }
        jedisClient.expire("SESSION:"+token,SESSION_EXPIRE);
        //取得用户信息并更新token过期时间
        TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
        return E3Result.ok(user);
    }
}
