package com.demo.mes.service;

import com.demo.mes.vo.DashboardOverviewVO;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    DashboardOverviewVO getOverview();
    List<Map<String, Object>> getWorkCenterStatus();
    List<Map<String, Object>> getOrderProgress();
    List<Map<String, Object>> getAlerts();
    Map<String, Object> getProductionDaily(String date);
    Map<String, Object> getQualityAnalysis(String startDate, String endDate);
    Map<String, Object> getOeeAnalysis();
}
