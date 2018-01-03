package com.jwt.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CrossSiteFilter implements Filter {

    public void destroy() {
        // TODO Auto-generated method stub

    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse) response;
//			HttpSession session = req.getSession();
        resp.addHeader("Access-Control-Allow-Origin", "http://192.168.1.116");
//			resp.addHeader("Access-Control-Allow-Origin", "http://192.168.1.170");
        resp.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.addHeader("Access-Control-Allow-Credentials","true");
        resp.addHeader("Access-Control-Allow-Headers", "x-requested-with,Content-Type");
        resp.addHeader("Access-Control-Max-Age", "1800");//30 min
        if (req.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(req.getMethod())) {

        }
        chain.doFilter(request, response);
    }

    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }

}
