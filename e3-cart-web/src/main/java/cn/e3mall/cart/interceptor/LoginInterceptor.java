package cn.e3mall.cart.interceptor;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    /**
    *用户登录处理拦截器
    */
    @Autowired
    private TokenService tokenService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 前处理，执行handler之前执行此方法。
        //返回true，放行	false：拦截
        //1.从cookie中取token
        String token = CookieUtils.getCookieValue(request, "cart");
        //2.如果没有token, 未登录状态,直接放行
        if (StringUtils.isBlank(token)){
            return true;
        }
        //3.取到token ,需调用sso系统的服务,格局token取用户信息
        E3Result result = tokenService.getUserByToken(token);
        //4.未取到用户信息,登录过期,放行.
        if (result.getStatus()!=200){
            return true;
        }
        //5.取到用户信息,登录状态
        TbUser user = (TbUser) result.getData();
        //6.将用户放到request中,只需要在Controller层中判断request中是否包含user信息,即可判断是否登录
        request.setAttribute("user",user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //handler执行之后，返回ModeAndView之前
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) throws Exception {
        //完成处理，返回ModelAndView之后。
        //可以再此处理异常
    }
}
