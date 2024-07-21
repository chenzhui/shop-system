package com.example.common.exception;

import com.example.common.msg.CodeMsg;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by wolfcode
 */
@Setter
@Getter
public class BusinessException extends RuntimeException {

    private CodeMsg codeMsg;

    public BusinessException(CodeMsg codeMsg) {
        super(codeMsg.getMsg());
        this.codeMsg = codeMsg;
    }
}
