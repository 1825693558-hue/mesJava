package com.demo.mes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.demo.mes.entity.*;
import com.demo.mes.mapper.*;
import com.demo.mes.service.DashboardService;
import com.demo.mes.vo.DashboardOverviewVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);

    private final ProductionOrderMapper productionOrderMapper;
    private final DispatchMapper dispatchMapper;
    private final WorkReportMapper workReportMapper;
    private final EquipmentMapper equipmentMapper;
    private final WorkCenterMapper workCenterMapper;
    private final ProcessStepMapper processStepMapper;
    private final SysUserMapper sysUserMapper;
    private final ProductMapper productMapper;

    @Autowired
    public DashboardServiceImpl(ProductionOrderMapper productionOrderMapper, DispatchMapper dispatchMapper, WorkReportMapper workReportMapper, EquipmentMapper equipmentMapper, WorkCenterMapper workCenterMapper, ProcessStepMapper processStepMapper, SysUserMapper sysUserMapper, ProductMapper productMapper) {
        this.productionOrderMapper = productionOrderMapper;
        this.dispatchMapper = dispatchMapper;
        this.workReportMapper = workReportMapper;
        this.equipmentMapper = equipmentMapper;
        this.workCenterMapper = workCenterMapper;
        this.processStepMapper = processStepMapper;
        this.sysUserMapper = sysUserMapper;
        this.productMapper = productMapper;
    }

    @Override
    public DashboardOverviewVO getOverview() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<ProductionOrder> todayOrders = productionOrderMapper.selectList(
                new LambdaQueryWrapper<ProductionOrder>()
                        .between(ProductionOrder::getPlannedStartTime, todayStart, todayEnd));

        int plannedQty = todayOrders.stream().mapToInt(ProductionOrder::getPlannedQty).sum();
        int completedQty = todayOrders.stream().mapToInt(ProductionOrder::getCompletedQty).sum();
        int scrapQty = todayOrders.stream().mapToInt(ProductionOrder::getScrapQty).sum();

        DashboardOverviewVO vo = new DashboardOverviewVO();
        vo.setPlannedQty(plannedQty);
        vo.setCompletedQty(completedQty);
        vo.setScrapQty(scrapQty);
        vo.setCompletionRate(plannedQty > 0 ? Math.round((double) completedQty / plannedQty * 100 * 100) / 100.0 : 0);
        vo.setScrapRate(completedQty > 0 ? Math.round((double) scrapQty / (completedQty + scrapQty) * 100 * 100) / 100.0 : 0);
        vo.setActiveOrderCount((int) todayOrders.stream().filter(o -> o.getStatus() == 2).count());

        List<Equipment> equipments = equipmentMapper.selectList(new LambdaQueryWrapper<>());
        vo.setTotalEquipmentCount(equipments.size());
        vo.setRunningEquipmentCount((int) equipments.stream().filter(e -> e.getStatus() == 1).count());

        return vo;
    }

    @Override
    public List<Map<String, Object>> getWorkCenterStatus() {
        List<WorkCenter> centers = workCenterMapper.selectList(new LambdaQueryWrapper<>());
        List<Map<String, Object>> result = new ArrayList<>();

        for (WorkCenter wc : centers) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("workCenterId", wc.getId());
            item.put("workCenterName", wc.getCenterName());
            item.put("status", wc.getStatus());
            item.put("statusText", wc.getStatus() == 1 ? "运行中" : "停机");

            List<Dispatch> activeDispatches = dispatchMapper.selectList(
                    new LambdaQueryWrapper<Dispatch>()
                            .eq(Dispatch::getWorkCenterId, wc.getId())
                            .in(Dispatch::getStatus, 0, 1, 2));
            item.put("activeDispatchCount", activeDispatches.size());

            List<Equipment> equipments = equipmentMapper.selectList(
                    new LambdaQueryWrapper<Equipment>().eq(Equipment::getWorkCenterId, wc.getId()));
            item.put("equipmentCount", equipments.size());
            item.put("runningEquipmentCount", equipments.stream().filter(e -> e.getStatus() == 1).count());

            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getOrderProgress() {
        List<ProductionOrder> activeOrders = productionOrderMapper.selectList(
                new LambdaQueryWrapper<ProductionOrder>()
                        .in(ProductionOrder::getStatus, 1, 2)
                        .orderByAsc(ProductionOrder::getPriority));

        List<Map<String, Object>> result = new ArrayList<>();
        for (ProductionOrder order : activeOrders) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("orderId", order.getId());
            item.put("orderNo", order.getOrderNo());

            Product product = productMapper.selectById(order.getProductId());
            item.put("productName", product != null ? product.getProductName() : "-");

            item.put("plannedQty", order.getPlannedQty());
            item.put("completedQty", order.getCompletedQty());
            item.put("scrapQty", order.getScrapQty());
            item.put("status", order.getStatus());
            item.put("statusText", getOrderStatusText(order.getStatus()));
            double progress = order.getPlannedQty() > 0
                    ? Math.round((double) order.getCompletedQty() / order.getPlannedQty() * 100 * 100) / 100.0 : 0;
            item.put("progress", progress);

            List<Dispatch> dispatches = dispatchMapper.selectList(
                    new LambdaQueryWrapper<Dispatch>().eq(Dispatch::getOrderId, order.getId()));
            item.put("totalSteps", dispatches.size());
            item.put("completedSteps", dispatches.stream().filter(d -> d.getStatus() == 3).count());

            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getAlerts() {
        List<Map<String, Object>> alerts = new ArrayList<>();

        List<ProductionOrder> laggingOrders = productionOrderMapper.selectList(
                new LambdaQueryWrapper<ProductionOrder>()
                        .eq(ProductionOrder::getStatus, 2)
                        .lt(ProductionOrder::getPlannedEndTime, LocalDateTime.now()));
        for (ProductionOrder order : laggingOrders) {
            Map<String, Object> alert = new LinkedHashMap<>();
            alert.put("type", "progress_lag");
            alert.put("level", "warning");
            alert.put("message", "订单 " + order.getOrderNo() + " 进度滞后，计划结束时间已过");
            alerts.add(alert);
        }

        List<Equipment> downEquipments = equipmentMapper.selectList(
                new LambdaQueryWrapper<Equipment>().in(Equipment::getStatus, 2, 3));
        for (Equipment eq : downEquipments) {
            Map<String, Object> alert = new LinkedHashMap<>();
            alert.put("type", "equipment_down");
            alert.put("level", "danger");
            alert.put("message", "设备 " + eq.getEquipmentName() + (eq.getStatus() == 2 ? "已停机" : "正在维修"));
            alerts.add(alert);
        }

        return alerts;
    }

    @Override
    public Map<String, Object> getProductionDaily(String date) {
        LocalDate targetDate = (date != null && !date.trim().isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
        LocalDateTime dayStart = targetDate.atStartOfDay();
        LocalDateTime dayEnd = targetDate.atTime(LocalTime.MAX);

        List<WorkReport> reports = workReportMapper.selectList(
                new LambdaQueryWrapper<WorkReport>()
                        .between(WorkReport::getReportTime, dayStart, dayEnd));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", targetDate.toString());
        result.put("totalGoodQty", reports.stream().mapToInt(WorkReport::getGoodQty).sum());
        result.put("totalScrapQty", reports.stream().mapToInt(WorkReport::getScrapQty).sum());
        result.put("reportCount", reports.size());

        Map<Long, List<WorkReport>> byDispatch = reports.stream()
                .collect(Collectors.groupingBy(WorkReport::getDispatchId));
        List<Map<String, Object>> detailList = new ArrayList<>();
        for (Map.Entry<Long, List<WorkReport>> entry : byDispatch.entrySet()) {
            Map<String, Object> detail = new LinkedHashMap<>();
            detail.put("dispatchId", entry.getKey());
            int goodQty = entry.getValue().stream().mapToInt(WorkReport::getGoodQty).sum();
            int scrapQty = entry.getValue().stream().mapToInt(WorkReport::getScrapQty).sum();
            detail.put("goodQty", goodQty);
            detail.put("scrapQty", scrapQty);
            detailList.add(detail);
        }
        result.put("details", detailList);

        return result;
    }

    @Override
    public Map<String, Object> getQualityAnalysis(String startDate, String endDate) {
        LambdaQueryWrapper<WorkReport> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null && !startDate.trim().isEmpty() && endDate != null && !endDate.trim().isEmpty()) {
            wrapper.between(WorkReport::getReportTime,
                    LocalDate.parse(startDate).atStartOfDay(),
                    LocalDate.parse(endDate).atTime(LocalTime.MAX));
        }
        List<WorkReport> reports = workReportMapper.selectList(wrapper);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalGoodQty", reports.stream().mapToInt(WorkReport::getGoodQty).sum());
        result.put("totalScrapQty", reports.stream().mapToInt(WorkReport::getScrapQty).sum());

        Map<String, Integer> reasonStats = new LinkedHashMap<>();
        for (WorkReport r : reports) {
            if (r.getScrapReason() != null && r.getScrapQty() > 0) {
                reasonStats.merge(r.getScrapReason(), r.getScrapQty(), Integer::sum);
            }
        }
        result.put("scrapReasonDistribution", reasonStats);

        return result;
    }

    @Override
    public Map<String, Object> getOeeAnalysis() {
        List<WorkCenter> centers = workCenterMapper.selectList(new LambdaQueryWrapper<>());
        List<Map<String, Object>> oeeList = new ArrayList<>();

        for (WorkCenter wc : centers) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("workCenterName", wc.getCenterName());

            List<Equipment> equipments = equipmentMapper.selectList(
                    new LambdaQueryWrapper<Equipment>().eq(Equipment::getWorkCenterId, wc.getId()));
            int runningCount = (int) equipments.stream().filter(e -> e.getStatus() == 1).count();
            double availability = equipments.isEmpty() ? 0 : (double) runningCount / equipments.size() * 100;

            List<Dispatch> todayDispatches = dispatchMapper.selectList(
                    new LambdaQueryWrapper<Dispatch>()
                            .eq(Dispatch::getWorkCenterId, wc.getId())
                            .ge(Dispatch::getActualStartTime,
                                    LocalDateTime.of(LocalDate.now(), LocalTime.MIN)));
            int totalGood = todayDispatches.stream().mapToInt(Dispatch::getCompletedQty).sum();

            double performance = wc.getCapacityPerHour().doubleValue() > 0
                    ? Math.min(100, totalGood / (wc.getCapacityPerHour().doubleValue() * 8) * 100) : 0;

            double quality = totalGood > 0
                    ? (double) todayDispatches.stream().mapToInt(Dispatch::getCompletedQty).sum()
                    / (totalGood + todayDispatches.stream().mapToInt(Dispatch::getScrapQty).sum()) * 100
                    : 100;

            double oee = availability * performance * quality / 10000;

            item.put("availability", Math.round(availability * 100) / 100.0);
            item.put("performance", Math.round(performance * 100) / 100.0);
            item.put("quality", Math.round(quality * 100) / 100.0);
            item.put("oee", Math.round(oee * 100) / 100.0);
            oeeList.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("workCenters", oeeList);
        return result;
    }

    private String getOrderStatusText(Integer status) {
        return switch (status) {
            case 0 -> "已创建";
            case 1 -> "已下发";
            case 2 -> "执行中";
            case 3 -> "已完成";
            case 4 -> "已关闭";
            default -> "未知";
        };
    }
}
