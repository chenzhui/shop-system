package com.example.common.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
public class IntergralPayVo implements Serializable {
    private String tradeNo; //交易编号
    private String orderNo;//订单编号
    private Long payAmount;//此次积分变动数值
    private String info;//备注
    private Long userId;//用户ID

    public IntergralPayVo(String orderNo,Long payAmount,String info,Long userId){
        this.orderNo=orderNo;
        this.payAmount=payAmount;
        this.info=info;
        this.userId=userId;
    }

    @Override
    public String toString() {
        return "IntergralPayVo{" +
                "tradeNo='" + tradeNo + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", payAmount=" + payAmount +
                ", info='" + info + '\'' +
                ", userId=" + userId +
                '}';
    }
}

