package com.demo.mes.controller;

import com.demo.mes.common.result.Result;
import com.demo.mes.service.DashboardService;
import com.demo.mes.vo.DashboardOverviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "车间看板")
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "当日生产概览")
    @GetMapping("/overview")
    public Result<DashboardOverviewVO> overview() {
        return Result.success(dashboardService.getOverview());
    }

    @Operation(summary = "工作中心实时状态")
    @GetMapping("/work-center-status")
    public Result<List<Map<String, Object>>> workCenterStatus() {
        return Result.success(dashboardService.getWorkCenterStatus());
    }

    @Operation(summary = "当日执行中订单进度")
    @GetMapping("/order-progress")
    public Result<List<Map<String, Object>>> orderProgress() {
        return Result.success(dashboardService.getOrderProgress());
    }

    @Operation(summary = "异常预警")
    @GetMapping("/alerts")
    public Result<List<Map<String, Object>>> alerts() {
        return Result.success(dashboardService.getAlerts());
    }

    @Operation(summary = "产量统计日报")
    @GetMapping("/report/production-daily")
    public Result<Map<String, Object>> productionDaily(@RequestParam(required = false) String date) {
        return Result.success(dashboardService.getProductionDaily(date));
    }

    @Operation(summary = "质量分析报表")
    @GetMapping("/report/quality-analysis")
    public Result<Map<String, Object>> qualityAnalysis(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(dashboardService.getQualityAnalysis(startDate, endDate));
    }

    @Operation(summary = "OEE分析报表")
    @GetMapping("/report/oee")
    public Result<Map<String, Object>> oee() {
        return Result.success(dashboardService.getOeeAnalysis());
    }
}
