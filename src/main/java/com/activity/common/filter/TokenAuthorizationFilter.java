package com.activity.common.filter;

import com.alibaba.fastjson.JSON;
import com.activity.common.beans.ResultBean;
import com.activity.common.utils.Jwt;
import com.activity.app.beans.user.User;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class TokenAuthorizationFilter implements Filter {

    private ResultBean msg;

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse) response;
        Enumeration headerNames = req.getHeaderNames();
        String url = req.getRequestURI();
        boolean commonFlag = url.contains("common");
        boolean loginFlag =url.contains("userLogin");
        boolean vcodeFlag =url.contains("getVcode");

        if(commonFlag || loginFlag ||vcodeFlag){
            chain.doFilter(req,resp);
        }
        String token = getValue("token",req.getCookies());
        User user = Jwt.unsign(token, User.class);
        if(null == user){
            resp.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            msg=new ResultBean(null,ResultBean.NO_LOGIN,"无效的token");
            writer.write(JSON.toJSONString(msg));
            writer.flush();
            writer.close();
        }else{
            chain.doFilter(req,resp);
        }

    }

    public void destroy() {

    }
    private String getValue(String cookieName,Cookie[] cookies){
        if(cookies!=null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(cookieName)){
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
