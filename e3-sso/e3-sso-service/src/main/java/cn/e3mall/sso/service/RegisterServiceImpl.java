package cn.e3mall.sso.service;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private TbUserMapper userMapper;

    //验证输入信息是否有误
    @Override
    public E3Result checkData(String param, int type) {
        //根据不同的type形成不同的查询条件
        TbUserExample userExample = new TbUserExample();
        TbUserExample.Criteria criteria = userExample.createCriteria();
        //1：用户名 2：手机号 3：邮箱
        if (1 == type) {
            criteria.andUsernameEqualTo(param);
        } else if (2 == type) {
            criteria.andPhoneEqualTo(param);
        } else if (3 == type) {
            criteria.andEmailEqualTo(param);
        } else {
            return E3Result.build(400, "数据类型错误!");
        }
        List<TbUser> users = this.userMapper.selectByExample(userExample);
        if (users != null && users.size() > 0) {
            //如果有数据返回false
            return E3Result.ok(false);
        }
        return E3Result.ok(true);
    }

    //无误后注册
    @Override
    public E3Result register(TbUser user) {
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())
                || StringUtils.isBlank(user.getPhone())) {
            return E3Result.build(400, "数据不完整,请检查!");
        }
        //1：用户名 2：手机号 3：邮箱
        E3Result result = checkData(user.getUsername(), 1);
        if (!(boolean) result.getData()) {
            return E3Result.build(400, "此用户名已被占用");
        }
        result = checkData(user.getPhone(), 2);
        if (!(boolean) result.getData()) {
            return E3Result.build(400, "手机号已经被占用");
        }
        result = checkData(user.getPhone(), 3);
        if (!(boolean) result.getData()) {
            return E3Result.build(400, "邮箱已被占用");
        }
        user.setCreated(new Date());
        user.setUpdated(new Date());
        //MD5加密
        String password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(password);
        this.userMapper.insert(user);
        return E3Result.ok();
    }
}