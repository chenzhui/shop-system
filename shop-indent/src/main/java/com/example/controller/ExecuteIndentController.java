package com.example.controller;

import com.example.common.CommonConstants;
import com.example.common.anno.RequireLogin;
import com.example.common.result.Result;
import com.example.common.utils.ResultUtil;
import com.example.common.entity.PayLog;
import com.example.common.entity.PayVo;
import com.example.feign.PaymentFeignApi;
import com.example.service.IndentSevice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/execute")
public class ExecuteIndentController {
    public static final int PAY_TYPE_ONLINE = 0;//在线支付
    public static final int PAY_TYPE_INTERGRAL = 1;//积分支付
    @Resource
    private IndentSevice indentSevice;

    @Resource
    private PaymentFeignApi paymentFeignApi;

    @RequireLogin
    @GetMapping("payForIndent")
    public Result payForIndent(@RequestParam("orderNo")String orderNo,
                               @RequestParam("payType")Integer payType,
                               @RequestHeader(CommonConstants.TOKEN_NAME) String token){
        if(PAY_TYPE_ONLINE==payType){
            return ResultUtil.success(indentSevice.payForZFB(orderNo));    //支付包支付
        }else{
            return ResultUtil.success(indentSevice.payForIntergral(orderNo,token));   //积分支付
        }
    }

    @GetMapping("/refundForIndent")
    public Result<String> refund(String orderNo, @RequestHeader("token") String token) {
        indentSevice.refund(orderNo, token);
        return ResultUtil.success("退款成功");
    }

    @GetMapping("/return_url")
    public void returnUrl(HttpServletRequest request, HttpServletResponse resp) throws Exception {
        // 获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用 valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        log.info("[订单支付] 收到同步回调请求，请求参数：{}", params);
        Result<Boolean> result = paymentFeignApi.checkRSASignature(params);// 远程调用支付服务进行签名验证
        if (result == null || result.hasError() || !result.getData()) {  // 重定向到签名失败页面
            resp.getWriter().write("<h1>支付宝同步回调签名验证失败</h1>");
            return;
        }
        // 从传回来的参数中获取订单编号
        String orderNo = request.getParameter("out_trade_no");


        resp.sendRedirect("http://localhost:5173/#/IndentInfo?orderNo=" + orderNo);  //如果签名验证通过，重定向到订单详情页
    }


    @PostMapping("/notify_url")
    public String notifyUrl(HttpServletRequest request, HttpServletResponse resp) throws Exception {   //异步
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用 valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        log.info("[订单支付] 收到异步回调请求，请求参数：{}", params);
        Result<Boolean> result = paymentFeignApi.checkRSASignature(params);   // 远程调用支付服务进行签名验证
        if (result == null || result.hasError() || !result.getData()) {return "error";}
        String orderNo = request.getParameter("out_trade_no");  //商户订单号
        String tradeNo = request.getParameter("trade_no");  //支付宝交易号
        String trade_status = request.getParameter("trade_status");  //交易状态
        String totalAmount = request.getParameter("total_amount");  //支付金额

        if ("TRADE_FINISHED".equals(trade_status)) {
            log.info("[订单支付] 订单已过退款时间，标记订单为完成状态，不可再退款");
        } else if ("TRADE_SUCCESS".equals(trade_status)) {
            indentSevice.alipaySuccess(orderNo, tradeNo, totalAmount);  //判断订单信息是否正确,并修改订单状态,添加日志
        }

        return "success";
    }


}
