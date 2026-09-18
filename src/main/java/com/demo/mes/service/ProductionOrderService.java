package com.demo.mes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.mes.dto.ProductionOrderDTO;
import com.demo.mes.entity.ProductionOrder;
import com.demo.mes.vo.OrderProgressVO;

public interface ProductionOrderService extends IService<ProductionOrder> {
    void createOrder(ProductionOrderDTO dto);
    void releaseOrder(Long orderId);
    void closeOrder(Long orderId);
    OrderProgressVO getOrderProgress(Long orderId);
}
