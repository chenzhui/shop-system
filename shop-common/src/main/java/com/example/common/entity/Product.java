package com.example.common.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;


@Setter@Getter
public class Product implements Serializable {
    private Long productId;//商品iD
    private String productName;//商品名称
    private String productTitle;//商品标题
    private String productImg;//商品图片
    private String productDetail;//商品明细
    private BigDecimal productPrice;//商品价格

    @Override
    public String toString() {
        return "Product{" +
                "id=" + productId +
                ", productName='" + productName + '\'' +
                ", productTitle='" + productTitle + '\'' +
                ", productImg='" + productImg + '\'' +
                ", productDetail='" + productDetail + '\'' +
                ", productPrice=" + productPrice +
                '}';
    }
}
