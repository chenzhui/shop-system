package com.example.mapper;

import com.example.common.entity.IntergralChangeLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IntergralMapper {
    int descIntergral(@Param("userId")Long userId,@Param(("value")) Long value);
    int incrIntergral(@Param("userId")Long userId,@Param(("value")) Long value);
    void insertLog(IntergralChangeLog intergralChangeLog);



}
