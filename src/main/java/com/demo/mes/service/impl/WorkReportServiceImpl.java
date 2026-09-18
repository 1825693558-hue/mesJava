package com.demo.mes.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mes.common.exception.BusinessException;
import com.demo.mes.dto.WorkReportDTO;
import com.demo.mes.entity.Dispatch;
import com.demo.mes.entity.ProductionOrder;
import com.demo.mes.entity.WorkReport;
import com.demo.mes.mapper.DispatchMapper;
import com.demo.mes.mapper.ProductionOrderMapper;
import com.demo.mes.mapper.WorkReportMapper;
import com.demo.mes.service.WorkReportService;
import com.demo.mes.vo.WorkReportResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class WorkReportServiceImpl extends ServiceImpl<WorkReportMapper, WorkReport>
        implements WorkReportService {

    private static final Logger log = LoggerFactory.getLogger(WorkReportServiceImpl.class);

    private final DispatchMapper dispatchMapper;
    private final ProductionOrderMapper productionOrderMapper;

    @Autowired
    public WorkReportServiceImpl(DispatchMapper dispatchMapper, ProductionOrderMapper productionOrderMapper) {
        this.dispatchMapper = dispatchMapper;
        this.productionOrderMapper = productionOrderMapper;
    }

    @Override
    @Transactional
    public WorkReportResultVO submitReport(WorkReportDTO dto, Long operatorId) {
        Dispatch dispatch = dispatchMapper.selectById(dto.getDispatchId());
        if (dispatch == null) {
            throw new BusinessException("派工单不存在");
        }
        if (dispatch.getStatus() == 3) {
            throw new BusinessException("该派工单已完成，不能继续报工");
        }

        int totalReported = dispatch.getCompletedQty() + dispatch.getScrapQty() + dto.getGoodQty() + dto.getScrapQty();
        if (totalReported > dispatch.getDispatchQty()) {
            throw new BusinessException("报工数量超出派工数量，已报工："
                    + (dispatch.getCompletedQty() + dispatch.getScrapQty())
                    + "，本次报工：" + (dto.getGoodQty() + dto.getScrapQty())
                    + "，派工数量：" + dispatch.getDispatchQty());
        }

        WorkReport report = new WorkReport();
        report.setDispatchId(dto.getDispatchId());
        report.setOperatorId(operatorId);
        report.setGoodQty(dto.getGoodQty());
        report.setScrapQty(dto.getScrapQty());
        report.setReworkQty(dto.getReworkQty());
        report.setScrapReason(dto.getScrapReason());
        report.setReportTime(LocalDateTime.now());
        report.setRemark(dto.getRemark());
        baseMapper.insert(report);

        dispatch.setCompletedQty(dispatch.getCompletedQty() + dto.getGoodQty());
        dispatch.setScrapQty(dispatch.getScrapQty() + dto.getScrapQty());
        if (dispatch.getStatus() == 0) {
            dispatch.setStatus(1);
            dispatch.setActualStartTime(LocalDateTime.now());
        }

        if (dispatch.getCompletedQty() + dispatch.getScrapQty() >= dispatch.getDispatchQty()) {
            dispatch.setStatus(3);
            dispatch.setActualEndTime(LocalDateTime.now());
        }

        dispatchMapper.updateById(dispatch);

        ProductionOrder order = productionOrderMapper.selectById(dispatch.getOrderId());
        if (order != null) {
            order.setCompletedQty(order.getCompletedQty() + dto.getGoodQty());
            order.setScrapQty(order.getScrapQty() + dto.getScrapQty());
            if (order.getStatus() == 1) {
                order.setStatus(2);
                order.setActualStartTime(LocalDateTime.now());
            }
            if (order.getCompletedQty() >= order.getPlannedQty()) {
                order.setStatus(3);
                order.setActualEndTime(LocalDateTime.now());
            }
            productionOrderMapper.updateById(order);
        }

        WorkReportResultVO result = new WorkReportResultVO();
        result.setReportId(report.getId());
        result.setDispatchCompletedQty(dispatch.getCompletedQty());
        result.setDispatchStatus(getStatusText(dispatch.getStatus()));
        result.setOrderCompletedQty(order != null ? order.getCompletedQty() : 0);
        result.setOrderProgress(order != null && order.getPlannedQty() > 0
                ? Math.round((double) order.getCompletedQty() / order.getPlannedQty() * 100 * 100) / 100.0
                : 0.0);

        return result;
    }

    private String getStatusText(Integer status) {
        return switch (status) {
            case 0 -> "待开工";
            case 1 -> "进行中";
            case 2 -> "已暂停";
            case 3 -> "已完成";
            default -> "未知";
        };
    }
}
