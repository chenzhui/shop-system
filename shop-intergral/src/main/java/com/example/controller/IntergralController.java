package com.example.controller;

import com.example.common.entity.IntergralChangeLog;
import com.example.common.entity.IntergralPayVo;
import com.example.common.entity.IntergralRefundVo;
import com.example.common.exception.BusinessException;
import com.example.common.msg.CodeMsg;
import com.example.common.result.Result;
import com.example.common.utils.IdGenerateUtil;
import com.example.common.utils.ResultUtil;
import com.example.mapper.IntergralMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/igPay")
public class IntergralController {
    @Resource
    private Redisson redisson;
    @Resource
    private IntergralMapper intergralMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/doPay")
    public String doPay(@RequestBody IntergralPayVo intergralPayVo){
        int row = intergralMapper.descIntergral(intergralPayVo.getUserId(),intergralPayVo.getPayAmount());
        if(row<=0){throw new BusinessException(new CodeMsg());}
        IntergralChangeLog intergralChangeLog=new IntergralChangeLog(IdGenerateUtil.get().nextId()+"",intergralPayVo.getOrderNo(),IntergralChangeLog.TYPE_DECR,intergralPayVo.getPayAmount(),intergralPayVo.getInfo());
        intergralMapper.insertLog(intergralChangeLog);
        return intergralChangeLog.getTradeNo();
    }

    @PostMapping("doRefund")
    public String doRefound(@RequestBody IntergralRefundVo intergralRefundVo){
        int row=intergralMapper.incrIntergral(intergralRefundVo.getUserId(),intergralRefundVo.getRefundAmount());  //增加用户积分
        if(row<=0){throw new BusinessException(new CodeMsg());}
        IntergralChangeLog intergralChangeLog=new IntergralChangeLog(IdGenerateUtil.get().nextId()+"",intergralRefundVo.getOrderNo(),IntergralChangeLog.TYPE_DECR,intergralRefundVo.getRefundAmount(),intergralRefundVo.getInfo());
        intergralMapper.insertLog(intergralChangeLog);
        return intergralChangeLog.getTradeNo();
    }

}
