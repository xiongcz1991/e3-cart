package cn.e3mall.sso.controller;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RegisterController {
    @Autowired
    private RegisterService registerService;
    @RequestMapping("/page/register")
    public String showRegister() {
        return "register";
    }
    //type 1 账号 2 手机 3 邮箱
    @RequestMapping("/user/check/{param}/{type}")
    @ResponseBody
    public E3Result checkData(@PathVariable String param ,@PathVariable Integer type){
        E3Result e3Result = this.registerService.checkData(param, type);
        return e3Result;
    }

    @RequestMapping(value="/user/register", method= RequestMethod.POST)
    @ResponseBody
    public E3Result register(TbUser user){
        E3Result e3Result = this.registerService.register(user);
        return e3Result;
    }
}
