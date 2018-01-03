package com.jwt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("common")
public class CommonController {
    @RequestMapping("commonInfo")
    @ResponseBody
    public String commonInfo(HttpServletResponse response){

        return "commonInfo";
    }
}
