package com.example.mapper;
import com.example.common.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface ProductMapper {
    List<Product> queryProductByIds(@Param("ids") List<Long> ids);
}
