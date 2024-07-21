package com.example.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.example.common.exception.BusinessException;
import com.example.common.mq.MQConstant;
import com.example.common.msg.CodeMsg;
import com.example.common.redis.CommonRedisKey;
import com.example.common.result.Result;
import com.example.common.entity.*;
import com.example.feign.IntergralFeignApi;
import com.example.feign.PaymentFeignApi;
import com.example.feign.SeckillFeignApi;
import com.example.mapper.IndentMapper;
import com.example.mapper.PayLogMapper;
import com.example.mapper.RefundLogMapper;
import com.example.msg.IndentCodeMsg;
import com.example.service.IndentSevice;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class IndentServiceImpl implements IndentSevice {
    @Resource
    private IndentMapper indentMapper;
    @Resource
    private PayLogMapper payLogMapper;

    @Resource
    private RefundLogMapper refundLogMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private PaymentFeignApi paymentFeignApi;
    @Resource
    private IntergralFeignApi intergralFeignApi;
    @Resource
    private SeckillFeignApi seckillFeignApi;
    @Resource
    private RocketMQTemplate rocketMQTemplate;




    @Override
    public List<OrderInfo> selectOrderInfoByUserId(String token) {
        UserInfo userInfo=this.getUserByToken(token);
        return indentMapper.selectOrderInfoByUserId(userInfo.getPhone());
    }


    @Override
    public String payForZFB(String orderNo) {
        OrderInfo orderInfo=indentMapper.selectOrderInfoByOrderNo(orderNo);//查询订单信息
        if(0==orderInfo.getStatus()){
            PayVo payVo=new PayVo();
            payVo.setBody("秒杀"+orderInfo.getProductName());
            payVo.setSubject(orderInfo.getProductName());
            payVo.setOutTradeNo(orderNo);
            payVo.setTotalAmount(orderInfo.getSeckillPrice().toString());
            payVo.setReturnUrl("http://vqm3ki.natappfree.cc/indent/execute/return_url");
            payVo.setNotifyUrl("http://vqm3ki.natappfree.cc/indent/execute/notify_url");
            Result<String> result=paymentFeignApi.doPay(payVo);  //调用支付接口发起支付
            return result.checkAndGet();
        }else{
            throw new BusinessException(new CodeMsg());
        }
    }

    @Override
    public Result doIntergralPay(IntergralPayVo intergralPayVo) {



        return null;
    }

    @Override
    public String payForIntergral(String orderNo,String token) {
        log.info("进入service");
        try{
            OrderInfo orderInfo=indentMapper.selectOrderInfoByOrderNo(orderNo);   //获取订单信息
            UserInfo userInfo=this.getUserByToken(token);  //获取用户信息
            if(0==orderInfo.getStatus() && orderInfo.getUserId().equals(userInfo.getPhone())){
                IntergralPayVo intergralPayVo=new IntergralPayVo(orderNo,orderInfo.getIntergral(),"积分支付",userInfo.getPhone());
                System.out.println(intergralPayVo.toString());
                String tradeNo=intergralFeignApi.doPay(intergralPayVo);   //修改用户积分
                //String tradeNo = result.checkAndGet();
                int row=indentMapper.changePayStatus(orderNo,OrderInfo.STATUS_ACCOUNT_PAID,OrderInfo.PAY_TYPE_INTERGRAL);  //改变订单状态
                if(row<=0){throw new BusinessException(new CodeMsg());}
                PayLog payLog=new PayLog(tradeNo,orderNo,intergralPayVo.getPayAmount()+"",PayLog.PAY_TYPE_INTERGRAL);
                payLogMapper.insertPayLog(payLog);    //生成支付日志
            }else {
                throw new BusinessException(new CodeMsg(1000,"非本人积分支付"));
            }
        }catch (Exception e){
            log.info("{}",e);
        }
        return "success";
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public void alipaySuccess(String orderNo, String tradeNo, String totalAmount) {
        OrderInfo orderInfo =indentMapper.selectOrderInfoByOrderNo(orderNo);  //查询订单信息
        if (orderInfo == null) {
            throw new BusinessException(IndentCodeMsg.REMOTE_DATA_ERROR); //数据查询异常
        }
        if (!orderInfo.getStatus().equals(OrderInfo.STATUS_ARREARAGE)) {   //判断订单状态是否正确
            throw new BusinessException(IndentCodeMsg.ORDER_STATUS_ERROR);
        }
        if (!orderInfo.getSeckillPrice().equals(new BigDecimal(totalAmount))) {  //判断订单支付金额是否正确
            throw new BusinessException(IndentCodeMsg.PAY_AMOUNT_ERROR);
        }
        try {
            PayLog log = this.buildPayLog(tradeNo, orderInfo, PayLog.PAY_TYPE_ONLINE);  //构建日志
            payLogMapper.insertPayLog(log);   //保存日志
            int row = indentMapper.changePayStatus(orderNo, OrderInfo.STATUS_ACCOUNT_PAID, OrderInfo.PAY_TYPE_ONLINE);  //更新订单支付成功状态
            if (row == 0) {
                throw new BusinessException(IndentCodeMsg.ORDER_STATUS_ERROR);
            }
        } catch (Exception e) {
            throw e;
            //throw new BusinessException(IndentCodeMsg.REPEAT_PAY_ERROR);
        }
    }

    @Override
    public OrderInfo selectOrderInfoByOrderNo(String orderNo) {
        return indentMapper.selectOrderInfoByOrderNo(orderNo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void refund(String orderNo, String token) {
        log.info("[退款请求] 收到退款请求 orderNo={}, token={}", orderNo, token);
        OrderInfo orderInfo = indentMapper.selectOrderInfoByOrderNo(orderNo);  //查询订单信息
        if (orderInfo == null) {
            throw new BusinessException(IndentCodeMsg.REMOTE_DATA_ERROR);
        }
        this.checkOrderUser(token, orderInfo);   //查询是否为订单拥有者
        if (!OrderInfo.STATUS_ACCOUNT_PAID.equals(orderInfo.getStatus())) {   //判断订单状态是否为已支付
            throw new BusinessException(IndentCodeMsg.ORDER_STATUS_ERROR);
        }
        if (orderInfo.getPayType() == OrderInfo.PAY_TYPE_ONLINE) {  //判断退钱还是退积分
            RefundVo vo = this.buildRefundVo(orderInfo);  //封装退款 vo 对象
            Result<Boolean> result = paymentFeignApi.refund(vo);  //远程调用支付服务，发起退款操作
            if (result == null || result.hasError() || !result.getData()) {   //判断退款结果是否成功
                log.warn("[退款操作] 支付宝退款失败：{}", JSON.toJSONString(result));
                throw new BusinessException(IndentCodeMsg.REFUND_ERROR);
            }
            this.changeRefundStatus(orderInfo, vo.getRefundReason());  // 改变订单状态
        } else{
            log.info("可以执行到事务");
            IntergralRefundVo iRefundVo = new IntergralRefundVo(orderInfo.getUserId(), orderNo, orderInfo.getIntergral(), "积分退款：" + orderInfo.getProductName(),"info");  //构建积分退款对象
            Message<IntergralRefundVo> message=MessageBuilder.withPayload(iRefundVo).setHeader("orderNo",orderNo).build();  //修改订单状态，并记录退款日志(本地事务) && 库存回补 && 修改用户积分,添加订单积分变动日志
            TransactionSendResult tResult=rocketMQTemplate.sendMessageInTransaction(MQConstant.INTERGRAL_REFUND_TX_TOPIC, message,orderNo);
            if(tResult.getLocalTransactionState().equals(LocalTransactionState.COMMIT_MESSAGE)||tResult.getLocalTransactionState().equals(LocalTransactionState.UNKNOW)){
                log.info("积分退款本地事务完成，等待远程服务执行状态{}",tResult.getLocalTransactionState());
            }else{
                log.info("{}",tResult.getLocalTransactionState());
            }

            // 如果本地事务执行状态返回了 ROLLBACK，就认为退款失败
//            if (result.getLocalTransactionState() == LocalTransactionState.ROLLBACK_MESSAGE) {
//                throw new BusinessException(IndentCodeMsg.REFUND_ERROR);
//            }
        }
    }


    @Override
    public void changeRefundStatus(OrderInfo orderInfo, String reason) {
        try{
            log.info("changeRefundStatus");
            System.out.println("cstart");
            int row = indentMapper.changeRefundStatus(orderInfo.getOrderNo(), OrderInfo.STATUS_REFUND);  //修改订单状态
            log.info("修改订单状态ok");
            if (row == 0) {throw new BusinessException(IndentCodeMsg.ORDER_STATUS_ERROR);}
            RefundLog rLog = this.buildRefundLog(orderInfo, reason); //记录退款日志
            refundLogMapper.insertRefundLog(rLog);  //将退款日志写入数据库
            log.info("退款日志写入数据库ok");
            System.out.println("cend");
        }catch (Exception e){
            throw e;
        }
    }


    private void checkOrderUser(String token, OrderInfo orderInfo) {
        // 判断当前用户是否是创建该订单的用户
        UserInfo userInfo = getUserByToken(token);
        if (userInfo == null) {
            throw new BusinessException(new CodeMsg(500103, "用户未认证"));
        }
        if (!userInfo.getPhone().equals(orderInfo.getUserId())) {
            throw new BusinessException(IndentCodeMsg.ILLEGAL_OPERATION);
        }
    }


    private UserInfo getUserByToken(String token) {
        String strObj = stringRedisTemplate.opsForValue().get(CommonRedisKey.USER_TOKEN.getRealKey(token));
        if (StringUtils.isEmpty(strObj)) {
            return null;
        }
        return JSON.parseObject(strObj, UserInfo.class);
    }

    private PayLog buildPayLog(String tradeNo, OrderInfo orderInfo, int payType) {
        PayLog log = new PayLog();
        log.setTradeNo(tradeNo);
        log.setOrderNo(orderInfo.getOrderNo());
        log.setPayType(payType);
        log.setStatus(PayLog.PAY_STATUS_SUCCESS);
        log.setNotifyTime(System.currentTimeMillis());
        if (payType == PayLog.PAY_TYPE_ONLINE) {
            log.setTotalAmount(orderInfo.getSeckillPrice().toString());
        } else {
            log.setTotalAmount(orderInfo.getIntergral() + "");
        }
        return log;
    }


    private RefundVo buildRefundVo(OrderInfo orderInfo) {
        RefundVo vo = new RefundVo();
        vo.setOutTradeNo(orderInfo.getOrderNo());
        vo.setRefundAmount(orderInfo.getSeckillPrice().toString());
        String type = OrderInfo.PAY_TYPE_ONLINE == orderInfo.getPayType() ? "支付宝" : "积分";
        vo.setRefundReason(type + "退款");

        return vo;
    }

    private RefundLog buildRefundLog(OrderInfo orderInfo, String reason) {
        RefundLog log = new RefundLog();
        log.setOutTradeNo(orderInfo.getOrderNo());
        if (orderInfo.getPayType() == OrderInfo.PAY_TYPE_INTERGRAL) {
            log.setRefundAmount(orderInfo.getIntergral().toString());
        } else {
            log.setRefundAmount(orderInfo.getSeckillPrice().toString());
        }
        log.setRefundReason(reason);
        log.setRefundTime(new Date());
        log.setRefundType(orderInfo.getPayType());
        return log;
    }



}
