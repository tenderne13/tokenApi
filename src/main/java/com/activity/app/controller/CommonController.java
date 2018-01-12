package com.activity.app.controller;

import com.activity.app.beans.user.User;
import com.activity.app.dao.user.UserMapper;
import com.activity.common.beans.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("common")
public class CommonController {

    @Autowired
    private UserMapper userMapper;



    @RequestMapping("commonInfo")
    @ResponseBody
    public ResultBean<User> commonInfo(HttpServletResponse response){
        Integer integer = userMapper.selectCount();
        User user = new User();
        user.setId(integer);
        return new ResultBean<User>(user);
    }

    @RequestMapping("info")
    @ResponseBody
    public ResultBean<User> commonInfo(){
        User user =new User("李小朋","123456");
        return new ResultBean<User>(user);
    }

}
