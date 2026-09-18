package com.demo.mes.vo;

public class WorkReportResultVO {
    private Long reportId;
    private Integer dispatchCompletedQty;
    private String dispatchStatus;
    private Integer orderCompletedQty;
    private Double orderProgress;

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Integer getDispatchCompletedQty() {
        return dispatchCompletedQty;
    }

    public void setDispatchCompletedQty(Integer dispatchCompletedQty) {
        this.dispatchCompletedQty = dispatchCompletedQty;
    }

    public String getDispatchStatus() {
        return dispatchStatus;
    }

    public void setDispatchStatus(String dispatchStatus) {
        this.dispatchStatus = dispatchStatus;
    }

    public Integer getOrderCompletedQty() {
        return orderCompletedQty;
    }

    public void setOrderCompletedQty(Integer orderCompletedQty) {
        this.orderCompletedQty = orderCompletedQty;
    }

    public Double getOrderProgress() {
        return orderProgress;
    }

    public void setOrderProgress(Double orderProgress) {
        this.orderProgress = orderProgress;
    }
}
