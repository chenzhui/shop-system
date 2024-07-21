package com.example.msg;


import com.example.common.msg.CodeMsg;

public class IndentCodeMsg extends CodeMsg {
    private IndentCodeMsg(Integer code, String msg) {
        super(code, msg);
    }

    public static final IndentCodeMsg REMOTE_DATA_ERROR = new IndentCodeMsg(500101, "数据查询异常.");
    public static final IndentCodeMsg SECKILL_STOCK_OVER = new IndentCodeMsg(500201, "您来晚了，商品已经被抢购完毕.");
    public static final IndentCodeMsg REPEAT_SECKILL = new IndentCodeMsg(500202, "您已经抢购到商品了，请不要重复抢购");
    public static final IndentCodeMsg OUT_OF_SECKILL_TIME_ERROR = new IndentCodeMsg(500209, "不在秒杀时间范围内!");
    public static final IndentCodeMsg SECKILL_ERROR = new IndentCodeMsg(500203, "秒杀失败");
    public static final IndentCodeMsg SECKILL_BUSY = new IndentCodeMsg(500303, "抢购人员过多，请稍后再试");
    public static final IndentCodeMsg CANCEL_ORDER_ERROR = new IndentCodeMsg(500204, "超时取消失败");
    public static final IndentCodeMsg PAY_SERVER_ERROR = new IndentCodeMsg(500205, "支付服务繁忙，稍后再试");
    public static final IndentCodeMsg REFUND_ERROR = new IndentCodeMsg(500206, "退款失败，请联系管理员");
    public static final IndentCodeMsg INTERGRAL_SERVER_ERROR = new IndentCodeMsg(500207, "操作积分失败");
    public static final CodeMsg REPEAT_PAY_ERROR = new IndentCodeMsg(500208, "请不要重复支付");
    public static final CodeMsg ORDER_STATUS_ERROR = new IndentCodeMsg(500209, "订单状态错误");
    public static final CodeMsg PAY_AMOUNT_ERROR = new IndentCodeMsg(500210, "支付金额不正确");
}
