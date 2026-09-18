package com.demo.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.mes.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
