package com.activity.app.controller;

import com.activity.app.beans.user.User;
import com.activity.common.beans.ResultBean;
import com.activity.common.utils.AuthUtils;
import com.activity.common.utils.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("user")
public class UserController {
    private static long maxAge = 1000*60*60;
    /*@RequestMapping("login")
    @ResponseBody
    public String login(User user){

        CookieStore cookieStore = loginUtil.loginAndGetCookie(user);
        if(cookieStore!=null){
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            user.setUuid(uuid);
            String sign = Jwt.sign(user, maxAge);
            ResponseData responseData = ResponseData.ok();
            responseData.putDataValue("token",sign);
            System.out.println(user);
            CookieContainer.setCookieStore(uuid,cookieStore);
            System.out.println("cookie容器中个数："+CookieContainer.getSize());
            return responseData.toJsonString();
        }
        System.out.println(user);
        return ResponseData.unauthorized().toJsonString();


    }*/

    @RequestMapping("userLogin")
    @ResponseBody
    public ResultBean<String> userLogin(User user){
        String token = Jwt.sign(user, maxAge);
        return new ResultBean<String>(token);
    }

    @RequestMapping("userInfo")
    @ResponseBody
    public ResultBean<User> userInfo(HttpServletRequest request){
        User user = AuthUtils.getUserInfo(request.getCookies());
        return new ResultBean<User>(user);
    }
}
