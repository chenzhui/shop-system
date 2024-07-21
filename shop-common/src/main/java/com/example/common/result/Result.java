package com.example.common.result;

import com.example.common.exception.BusinessException;
import com.example.common.msg.CodeMsg;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable {
    private Integer statusCode;

    private String msg;

    private T data;
    public boolean hasError(){
        return this.statusCode != 200;
    }

    public static <T> Result<T> error(CodeMsg codeMsg){
        return new Result<>(codeMsg.getCode(), codeMsg.getMsg(), null);
    }
    public static <T> Result<T> defaultError(){
        return new Result<>(5000,"错误",null);
    }

    public T checkAndGet(){
        if(!hasError()){
            return this.data;
        }else{
            throw new BusinessException(new CodeMsg());
        }
    }
}