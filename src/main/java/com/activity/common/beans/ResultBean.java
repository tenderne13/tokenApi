package com.activity.common.beans;

import com.alibaba.fastjson.JSON;
import com.activity.common.utils.page.Page;
import com.activity.app.beans.user.User;
import java.util.ArrayList;

public class ResultBean<T> {
    public static final int NO_LOGIN = -1;

    public static final int SUCCESS = 0;

    public static final int FAIL = 1;

    public static final int BADREQUEST=400;

    public static final int NO_PERMISSION = 2;

    private String msg = "success";

    private int code = SUCCESS;

    private T data;

    public ResultBean() {
        super();
    }

    public ResultBean(T data) {
        super();
        this.data = data;
    }

    public ResultBean(T data, int code, String msg){
        this.data = data;
        this.code = code;
        this.msg=msg;
    }

    public ResultBean(Throwable e) {
        super();
        this.msg = e.toString();
        this.code = FAIL;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultBean{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", data=" + data +
                '}';
    }

    public static void main(String[] args){
        User user = new User("lxp","123");
        Page<User> page =new Page<User>();
        ArrayList<User> users = new ArrayList<User>();
        users.add(user);
        page.setRecords(users);
        ResultBean<String> resultBean = new ResultBean<String>("1234");
        System.out.println(JSON.toJSONString(resultBean));
    }
}
