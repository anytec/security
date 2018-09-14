package cn.anytec.security.interceptor;

import cn.anytec.security.core.enums.SecurityExceptionEnum;
import cn.anytec.security.core.exception.BussinessException;
import cn.anytec.security.model.TbUser;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean flag =true;
        TbUser user = (TbUser) request.getSession().getAttribute("currentUser");
        if(null == user){
            throw new BussinessException(SecurityExceptionEnum.UNAUTHORIZED);
        }else {
            flag = true;
        }
        return flag;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

    }
}
