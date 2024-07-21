package com.example.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.example.common.entity.PayVo;
import com.example.common.entity.RefundVo;
import com.example.common.exception.BusinessException;
import com.example.common.msg.CodeMsg;
import com.example.common.result.Result;
import com.example.common.utils.ResultUtil;
import com.example.config.AlipayConfig;
import com.example.config.AlipayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("alipay")
public class ALipayController {

    @Resource
    private AlipayConfig alipayConfig;

    @Resource
    private AlipayProperties alipayProperties;

    @Resource
    private AlipayClient alipayClient;

    @PostMapping("/doPay")
    public Result<String> doPay(@RequestBody PayVo vo) {
        try {
            AlipayTradePagePayRequest request = this.buildRequest(vo); //设置
            String body = alipayClient.pageExecute(request).getBody();// 请求到支付宝以后，预订单创建成功，会返回一个 HTML 片段，实现跳转到支付宝页面
            return ResultUtil.success(body);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(new CodeMsg(500401, e.getMessage()));
        }
    }

    @PostMapping("/refund")
    public Result<Boolean> refund(@RequestBody RefundVo vo) {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();  // 创建退款请求对象
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", vo.getOutTradeNo());
        bizContent.put("refund_amount", vo.getRefundAmount());
        bizContent.put("refund_reason", vo.getRefundReason());
        request.setBizContent(bizContent.toString());  // 构建请求参数
        try {
            AlipayTradeRefundResponse response = alipayClient.execute(request);  //向支付宝接口发起退款请求
            log.info("[支付宝接口] 获取退款响应结果：{}", JSON.toJSONString(response));
            if (!"10000".equals(response.getCode())) {
                throw new BusinessException(new CodeMsg(500500, response.getMsg()));
            }
            if (!"Y".equals(response.getFundChange())) {  // 判断是否退款成功
                AlipayTradeFastpayRefundQueryRequest refundQueryRequest = new AlipayTradeFastpayRefundQueryRequest();
                bizContent = new JSONObject();
                bizContent.put("out_trade_no", vo.getOutTradeNo());
                bizContent.put("out_request_no", vo.getOutTradeNo());
                refundQueryRequest.setBizContent(bizContent.toString());
                AlipayTradeFastpayRefundQueryResponse refundQueryResponse = alipayClient.execute(refundQueryRequest);  // 如果未收到 fundChange=Y，也不代表退款一定失败，可以再次调用退款查询接口查询是否真正退款成功
                log.info("[支付宝接口] 查询退款结果：{}", JSON.toJSONString(refundQueryResponse));

                if (!"REFUND_SUCCESS".equals(refundQueryResponse.getRefundStatus())) {
                    // 查询到的也是退款失败
                    return ResultUtil.success(false);
                }
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return ResultUtil.success(true);
    }

    @PostMapping("/checkRSASignature")
    public Result<Boolean> checkRSASignature(@RequestBody Map<String, String> params) throws AlipayApiException {
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayProperties.getAlipayPublicKey(), alipayProperties.getCharset(), alipayProperties.getSignType());
        log.info("公钥私钥是否正确");
        return ResultUtil.success(signVerified);
    }

    private AlipayTradePagePayRequest buildRequest(PayVo vo) {  // 设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(vo.getReturnUrl());
        alipayRequest.setNotifyUrl(vo.getNotifyUrl());
        JSONObject json = new JSONObject();
        json.put("out_trade_no", vo.getOutTradeNo());
        json.put("total_amount", vo.getTotalAmount());
        json.put("subject", vo.getSubject());
        json.put("body", vo.getBody());
        json.put("product_code", "FAST_INSTANT_TRADE_PAY");
        alipayRequest.setBizContent(json.toJSONString());
        return alipayRequest;
    }
}
