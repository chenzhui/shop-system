package com.example.service.impl;

import com.example.common.entity.IntergralChangeLog;
import com.example.common.entity.IntergralRefundVo;
import com.example.common.exception.BusinessException;
import com.example.common.msg.CodeMsg;
import com.example.common.utils.IdGenerateUtil;
import com.example.mapper.IntergralMapper;
import com.example.service.IntergralService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class IntergralServiceImpl implements IntergralService {

    @Resource
    private IntergralMapper intergralMapper;
    @Override
    public String doRefound(IntergralRefundVo intergralRefundVo) {
        int row=intergralMapper.incrIntergral(intergralRefundVo.getUserId(),intergralRefundVo.getRefundAmount());  //增加用户积分
        if(row<=0){throw new BusinessException(new CodeMsg());}
        IntergralChangeLog intergralChangeLog=new IntergralChangeLog(IdGenerateUtil.get().nextId()+"",intergralRefundVo.getOrderNo(),IntergralChangeLog.TYPE_DECR,intergralRefundVo.getRefundAmount(),intergralRefundVo.getInfo());
        intergralMapper.insertLog(intergralChangeLog);
        return intergralChangeLog.getTradeNo();
    }
}
