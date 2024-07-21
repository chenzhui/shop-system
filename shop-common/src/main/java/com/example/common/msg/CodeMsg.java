package com.example.common.msg;

import lombok.*;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeMsg implements Serializable {
    private Integer code;
    private String msg;

    public static final CodeMsg ILLEGAL_OPERATION = new CodeMsg(500403, "非法操作");



}
