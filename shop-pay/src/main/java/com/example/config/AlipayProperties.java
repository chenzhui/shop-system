package com.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayProperties {
    private String appId;
    private String merchantPrivateKey;//应用私钥
    private String alipayPublicKey;// 支付宝公钥
    private String signType;  // 签名方式
    private String charset;  // 字符编码格式
    private String gatewayUrl;  // 支付宝网关
    private String returnUrl;
    private String notifyUrl;
}
