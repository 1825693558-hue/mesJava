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

        int goodQty = dto.getGoodQty() != null ? dto.getGoodQty() : 0;
        int scrapQty = dto.getScrapQty() != null ? dto.getScrapQty() : 0;
        int reworkQty = dto.getReworkQty() != null ? dto.getReworkQty() : 0;

        // 原子累加派工单数量，WHERE 条件保证并发下不超派工数量
        int dispatchUpdated = dispatchMapper.addReportQty(dto.getDispatchId(), goodQty, scrapQty);
        if (dispatchUpdated == 0) {
            throw new BusinessException("报工数量超出派工数量，已报工："
                    + (dispatch.getCompletedQty() + dispatch.getScrapQty())
                    + "，本次报工：" + (goodQty + scrapQty)
                    + "，派工数量：" + dispatch.getDispatchQty());
        }

        // 插入报工记录
        WorkReport report = new WorkReport();
        report.setDispatchId(dto.getDispatchId());
        report.setOperatorId(operatorId);
        report.setGoodQty(goodQty);
        report.setScrapQty(scrapQty);
        report.setReworkQty(reworkQty);
        report.setScrapReason(dto.getScrapReason());
        report.setReportTime(LocalDateTime.now());
        report.setRemark(dto.getRemark());
        baseMapper.insert(report);

        // 重新查询派工单最新数量，据此更新状态
        Dispatch latestDispatch = dispatchMapper.selectById(dto.getDispatchId());
        LocalDateTime now = LocalDateTime.now();

        if (latestDispatch.getStatus() == 0) {
            dispatchMapper.updateStatusInfo(latestDispatch.getId(), 1, now, null);
        }
        if (latestDispatch.getCompletedQty() + latestDispatch.getScrapQty() >= latestDispatch.getDispatchQty()) {
            dispatchMapper.updateStatusInfo(latestDispatch.getId(), 3, now, now);
        }

        // 原子累加订单数量
        ProductionOrder order = productionOrderMapper.selectById(latestDispatch.getOrderId());
        if (order != null) {
            productionOrderMapper.addReportQty(order.getId(), goodQty, scrapQty);

            ProductionOrder latestOrder = productionOrderMapper.selectById(order.getId());
            if (latestOrder.getStatus() == 1) {
                productionOrderMapper.updateStatusInfo(latestOrder.getId(), 2, now, null);
            }
            // Bug 修复：订单完成判断必须同时考虑合格数 + 报废数
            if (latestOrder.getCompletedQty() + latestOrder.getScrapQty() >= latestOrder.getPlannedQty()) {
                productionOrderMapper.updateStatusInfo(latestOrder.getId(), 3, now, now);
            }
        }

        // 返回最终状态
        Dispatch finalDispatch = dispatchMapper.selectById(dto.getDispatchId());
        ProductionOrder finalOrder = order != null ? productionOrderMapper.selectById(order.getId()) : null;

        WorkReportResultVO result = new WorkReportResultVO();
        result.setReportId(report.getId());
        result.setDispatchCompletedQty(finalDispatch.getCompletedQty());
        result.setDispatchStatus(getStatusText(finalDispatch.getStatus()));
        result.setOrderCompletedQty(finalOrder != null ? finalOrder.getCompletedQty() : 0);
        result.setOrderProgress(finalOrder != null && finalOrder.getPlannedQty() > 0
                ? Math.round((double) finalOrder.getCompletedQty() / finalOrder.getPlannedQty() * 100 * 100) / 100.0
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
