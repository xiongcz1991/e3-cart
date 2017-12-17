package cn.e3mall.sso.service;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.UUID;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private JedisClient jedisClient;
    @Value("${SESSION_EXPIRE}")
    private Integer SESSION_EXPIRE;
    @Override
    public E3Result userLogin(String username, String password) {
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<TbUser> users = this.userMapper.selectByExample(example);
        if (users==null&&users.size()==0) {
            //返回登录失败
            return E3Result.build(400, "用户名或密码错误");
        }
        //用户存在
        TbUser user = users.get(0);
        String md5password = DigestUtils.md5DigestAsHex(password.getBytes());
        //判断密码是否正确
        if (!md5password.equals(user.getPassword())){
            //返回登录失败
            return E3Result.build(400, "用户名或密码错误");
        }
        //登录成功 ,生成token
        String token = UUID.randomUUID().toString();
        //将用户信息写入redis缓存
        jedisClient.set("SESSION:"+token, JsonUtils.objectToJson(user));
        //设置过期时间        1800秒
        jedisClient.expire("SESSION:"+token,SESSION_EXPIRE);
      //jedisClient.expire("SESSION:" + token, SESSION_EXPIRE);
        //把token返回
        return E3Result.ok(token);
    }
}
