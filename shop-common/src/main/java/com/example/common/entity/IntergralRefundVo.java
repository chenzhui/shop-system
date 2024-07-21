package com.example.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author xiaoliu
 * @date 2023/6/13
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IntergralRefundVo {

    private Long userId;
    private String orderNo;
    private Long refundAmount;
    private String refundReason;
    private String info;
}
