package cn.e3mall.order.interceptor;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//用户登录拦截器
public class LoginInterceptor implements HandlerInterceptor{
    @Value("${SSO_URL}")
    private String SSO_URL; //http://localhost:8088

    @Autowired
    private TokenService tokenService;
    @Autowired
    private CartService cartService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中取token
        String token = CookieUtils.getCookieValue(request, "token");
        //判断token是否存在
        if (StringUtils.isBlank(token)){
            //未登录状态下 , 跳转到sso系统的登录界面
            //且用户登录成功后跳转到当前页面
            response.sendRedirect(SSO_URL+"/page/login?redirect="
                    +request.getRequestURL());  //获得当前页面URL
            return false;   //拦截
        }
        //token存在,调用sso系统服务,判断token是否过期
        E3Result result = tokenService.getUserByToken(token);
        //如果取不到，用户登录已经过期，需要登录。
        if (result.getStatus()!=200){
            response.sendRedirect(SSO_URL+"/page/login?redirect="
                    +request.getRequestURL());  //获得当前页面URL
            return false;   //拦截
        }
        //如果取到用户数据,是登录状态,需要将用户信息写入request
        TbUser user = (TbUser) result.getData();
        request.setAttribute("user",user);
        //判断cookie中是否有购物车数据,若有将其合并到服务端
        String cart = CookieUtils.getCookieValue(request, "cart");
        if (StringUtils.isNotBlank(cart)){
            //合并购物车
            cartService.mergeCart(user.getId(), JsonUtils.jsonToList(cart, TbItem.class));
        }
        //放行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
