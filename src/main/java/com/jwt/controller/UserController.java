package com.jwt.controller;

import com.alibaba.fastjson.JSON;
import com.jwt.Entity.ResponseData;
import com.jwt.Entity.User;
import com.jwt.utils.AuthUtils;
import com.jwt.utils.CookieContainer;
import com.jwt.utils.Jwt;
import org.apache.http.client.CookieStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import zz.pseas.ghost.login.zhihu.HttpClientUtil;
import zz.pseas.ghost.login.zhihu.ZhiHuMainLogin;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@RequestMapping("user")
public class UserController {
    private static long maxAge = 1000*60*60;
    private static ZhiHuMainLogin zhihuLogin = new ZhiHuMainLogin();
    @RequestMapping("login")
    @ResponseBody
    public String login(User user){

        CookieStore cookieStore = zhihuLogin.loginAndGetCookieStore(user.getUsername(), user.getPassword());
        if(cookieStore!=null){
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            user.setUuid(uuid);
            String sign = Jwt.sign(user, maxAge);
            ResponseData responseData = ResponseData.ok();
            responseData.putDataValue("token",sign);
            CookieContainer.setCookieStore(uuid,cookieStore);
            return responseData.toJsonString();
        }
        System.out.println(user);
        return ResponseData.unauthorized().toJsonString();


    }

    @RequestMapping("userInfo")
    @ResponseBody
    public String userInfo(HttpServletRequest request){
        User userInfo = AuthUtils.getUserInfo(request);
        CookieStore cookieStore = AuthUtils.getCookieStoreByUid(request);
        if(cookieStore!=null){
            String url = "https://www.zhihu.com/people/tenderne13-49/activities";
            String result = HttpClientUtil.doGet(url, cookieStore);
            ResponseData responseData = ResponseData.ok();
            responseData.putDataValue("result",result);
            return responseData.toJsonString();
        }
        return ResponseData.unauthorized().toJsonString();
    }
}
