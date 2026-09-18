package com.demo.mes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mes.common.exception.BusinessException;
import com.demo.mes.dto.ProductionOrderDTO;
import com.demo.mes.entity.*;
import com.demo.mes.mapper.*;
import com.demo.mes.service.ProductionOrderService;
import com.demo.mes.vo.DispatchProgressVO;
import com.demo.mes.vo.OrderProgressVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ProductionOrderServiceImpl extends ServiceImpl<ProductionOrderMapper, ProductionOrder>
        implements ProductionOrderService {

    private static final Logger log = LoggerFactory.getLogger(ProductionOrderServiceImpl.class);

    private final ProductMapper productMapper;
    private final ProcessStepMapper processStepMapper;
    private final WorkCenterMapper workCenterMapper;
    private final DispatchMapper dispatchMapper;
    private final SysUserMapper sysUserMapper;

    @Autowired
    public ProductionOrderServiceImpl(ProductMapper productMapper, ProcessStepMapper processStepMapper, WorkCenterMapper workCenterMapper, DispatchMapper dispatchMapper, SysUserMapper sysUserMapper) {
        this.productMapper = productMapper;
        this.processStepMapper = processStepMapper;
        this.workCenterMapper = workCenterMapper;
        this.dispatchMapper = dispatchMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    @Transactional
    public void createOrder(ProductionOrderDTO dto) {
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        if (product.getRouteId() == null) {
            throw new BusinessException("该产品未关联工艺路线，无法创建订单");
        }

        ProductionOrder order = new ProductionOrder();
        order.setOrderNo(generateOrderNo());
        order.setProductId(dto.getProductId());
        order.setPlannedQty(dto.getPlannedQty());
        order.setCompletedQty(0);
        order.setScrapQty(0);
        order.setPriority(dto.getPriority());
        order.setStatus(0);
        order.setPlannedStartTime(dto.getPlannedStartTime());
        order.setPlannedEndTime(dto.getPlannedEndTime());
        baseMapper.insert(order);
    }

    @Override
    @Transactional
    public void releaseOrder(Long orderId) {
        ProductionOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("生产订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("只有[已创建]状态的订单才能下发");
        }

        Product product = productMapper.selectById(order.getProductId());
        if (product == null || product.getRouteId() == null) {
            throw new BusinessException("产品或工艺路线不存在");
        }

        List<ProcessStep> steps = processStepMapper.selectList(
                new LambdaQueryWrapper<ProcessStep>()
                        .eq(ProcessStep::getRouteId, product.getRouteId())
                        .orderByAsc(ProcessStep::getStepNo));

        if (steps.isEmpty()) {
            throw new BusinessException("工艺路线下没有工序，无法下发");
        }

        for (ProcessStep step : steps) {
            Dispatch dispatch = new Dispatch();
            dispatch.setDispatchNo(generateDispatchNo(order.getOrderNo(), step.getStepNo()));
            dispatch.setOrderId(order.getId());
            dispatch.setStepId(step.getId());
            dispatch.setWorkCenterId(step.getWorkCenterId());
            dispatch.setOperatorId(null);
            dispatch.setDispatchQty(order.getPlannedQty());
            dispatch.setCompletedQty(0);
            dispatch.setScrapQty(0);
            dispatch.setStatus(0);
            dispatch.setPlannedStartTime(order.getPlannedStartTime());
            dispatch.setPlannedEndTime(order.getPlannedEndTime());
            dispatchMapper.insert(dispatch);
        }

        order.setStatus(1);
        baseMapper.updateById(order);
    }

    @Override
    @Transactional
    public void closeOrder(Long orderId) {
        ProductionOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("生产订单不存在");
        }
        if (order.getStatus() != 3) {
            throw new BusinessException("只有[已完成]状态的订单才能关闭");
        }
        order.setStatus(4);
        order.setActualEndTime(LocalDateTime.now());
        baseMapper.updateById(order);
    }

    @Override
    public OrderProgressVO getOrderProgress(Long orderId) {
        ProductionOrder order = baseMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("生产订单不存在");
        }

        Product product = productMapper.selectById(order.getProductId());

        List<Dispatch> dispatches = dispatchMapper.selectList(
                new LambdaQueryWrapper<Dispatch>()
                        .eq(Dispatch::getOrderId, orderId)
                        .orderByAsc(Dispatch::getDispatchNo));

        List<DispatchProgressVO> dispatchVOs = new ArrayList<>();
        for (Dispatch d : dispatches) {
            DispatchProgressVO vo = new DispatchProgressVO();
            vo.setDispatchId(d.getId());
            vo.setDispatchNo(d.getDispatchNo());

            ProcessStep step = processStepMapper.selectById(d.getStepId());
            vo.setStepName(step != null ? step.getStepName() : "-");

            WorkCenter wc = workCenterMapper.selectById(d.getWorkCenterId());
            vo.setWorkCenterName(wc != null ? wc.getCenterName() : "-");

            if (d.getOperatorId() != null) {
                SysUser user = sysUserMapper.selectById(d.getOperatorId());
                vo.setOperatorName(user != null ? user.getRealName() : "-");
            } else {
                vo.setOperatorName("未指派");
            }

            vo.setDispatchQty(d.getDispatchQty());
            vo.setCompletedQty(d.getCompletedQty());
            vo.setScrapQty(d.getScrapQty());
            vo.setStatus(d.getStatus());
            vo.setStatusText(getDispatchStatusText(d.getStatus()));
            double progress = d.getDispatchQty() > 0
                    ? (double) d.getCompletedQty() / d.getDispatchQty() * 100 : 0;
            vo.setProgress(Math.round(progress * 100) / 100.0);
            dispatchVOs.add(vo);
        }

        OrderProgressVO result = new OrderProgressVO();
        result.setOrderId(order.getId());
        result.setOrderNo(order.getOrderNo());
        result.setProductName(product != null ? product.getProductName() : "-");
        result.setPlannedQty(order.getPlannedQty());
        result.setCompletedQty(order.getCompletedQty());
        result.setScrapQty(order.getScrapQty());
        double progress = order.getPlannedQty() > 0
                ? (double) order.getCompletedQty() / order.getPlannedQty() * 100 : 0;
        result.setProgress(Math.round(progress * 100) / 100.0);
        result.setDispatches(dispatchVOs);

        return result;
    }

    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = baseMapper.selectCount(new LambdaQueryWrapper<>());
        return "MO" + dateStr + String.format("%03d", count + 1);
    }

    private String generateDispatchNo(String orderNo, Integer stepNo) {
        return orderNo + "-" + String.format("%02d", stepNo);
    }

    private String getDispatchStatusText(Integer status) {
        return switch (status) {
            case 0 -> "待开工";
            case 1 -> "进行中";
            case 2 -> "已暂停";
            case 3 -> "已完成";
            default -> "未知";
        };
    }
}
