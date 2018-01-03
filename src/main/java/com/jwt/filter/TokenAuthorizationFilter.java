package com.jwt.filter;

import com.alibaba.fastjson.JSON;
import com.jwt.Entity.User;
import com.jwt.utils.Jwt;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class TokenAuthorizationFilter implements Filter {

    private Map<String,Object> msg = new HashMap<String, Object>();

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse) response;
        Enumeration headerNames = req.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = (String) headerNames.nextElement();
            String value = req.getHeader(name);
            System.out.println(name+"="+value);
        }
        String url = req.getRequestURI();
        boolean commonFlag = url.contains("common");
        boolean loginFlag =url.contains("login");

        if(commonFlag || loginFlag){
            chain.doFilter(req,resp);
        }
        String token = getValue("token",req.getCookies());
        User user = Jwt.unsign(token, User.class);
        if(null == user){
            PrintWriter writer = resp.getWriter();
            msg.put("code",403);
            msg.put("message","无效的token");
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
