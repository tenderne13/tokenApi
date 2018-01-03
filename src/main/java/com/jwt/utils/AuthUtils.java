package com.jwt.utils;

import com.jwt.Entity.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class AuthUtils {

    //获取cookie值
    public static String getValue(String cookieName,Cookie[] cookies){
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookieName)){
                return cookie.getValue();
            }
        }
        return null;
    }

    //获取token
    public static String getToken(HttpServletRequest request){
        return getValue("token",request.getCookies());
    }

    //解析token中的信息
    public static User getUserInfo(HttpServletRequest request){
        String token = getToken(request);
        return Jwt.unsign(token, User.class);
    }
}