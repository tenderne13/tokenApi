package com.jwt.controller;

import com.alibaba.fastjson.JSON;
import com.jwt.Entity.ResponseData;
import com.jwt.Entity.User;
import com.jwt.utils.AuthUtils;
import com.jwt.utils.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("user")
public class UserController {
    private static long maxAge = 1000*60*60;
    @RequestMapping("login")
    @ResponseBody
    public String login(){
        User user = new User("lxp","123456");
        String sign = Jwt.sign(user, maxAge);
        ResponseData responseData = ResponseData.ok();
        responseData.putDataValue("token",sign);
        return responseData.toJsonString();
    }

    @RequestMapping("userInfo")
    @ResponseBody
    public String userInfo(HttpServletRequest request){
        User userInfo = AuthUtils.getUserInfo(request);
        ResponseData responseData = ResponseData.ok();
        responseData.putDataValue("user",userInfo);
        return responseData.toJsonString();
    }
}
