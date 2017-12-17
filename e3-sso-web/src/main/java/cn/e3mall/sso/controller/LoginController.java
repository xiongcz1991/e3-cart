package cn.e3mall.sso.controller;


import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.sso.service.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Value("TOKEN_KEY")
    private String TOKEN_KEY; //"token"


    @RequestMapping("/page/login")
    public String showLogin(String redirect, Model model) {     //实现order模块拦截器的回调
        if (StringUtils.isNotBlank(redirect)){
            model.addAttribute("redirect",redirect);
        }
        return "login";
    }

    @RequestMapping(value="/user/login", method= RequestMethod.POST)
    @ResponseBody
    public E3Result login(String username, String password, HttpServletRequest request, HttpServletResponse response){
        E3Result result = this.loginService.userLogin(username, password);
        //登录成功,把token写入cookie
        if (result.getStatus()==200){
            String token = result.getData().toString();
            //将token放入cookie
            CookieUtils.setCookie(request, response, TOKEN_KEY, token);
        }
        return result;
    }
}
