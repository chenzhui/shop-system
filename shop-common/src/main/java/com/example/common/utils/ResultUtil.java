package com.example.common.utils;


import com.example.common.result.Result;

public class ResultUtil {
    public static Result success(Object object) {
        Result rst = new Result();
        rst.setStatusCode(Integer.valueOf(200));
        rst.setMsg("success");
        rst.setData(object);
        return rst;
    }

    public static Result success(int i) {
        Result rst = new Result();
        rst.setStatusCode(Integer.valueOf(200));
        rst.setMsg("success");
        rst.setData(Integer.valueOf(i));
        return rst;
    }

    public static Result success(String s) {
        Result rst = new Result();
        rst.setStatusCode(Integer.valueOf(200));
        rst.setMsg("success");
        rst.setData(s);
        return rst;
    }

    public static Result redirect() {
        Result result = new Result();
        result.setStatusCode(Integer.valueOf(401));
        result.setData(Integer.valueOf(0));
        return result;
    }
}
