package com.demo.mes.vo;

public class DispatchProgressVO {
    private Long dispatchId;
    private String dispatchNo;
    private String stepName;
    private String workCenterName;
    private String operatorName;
    private Integer dispatchQty;
    private Integer completedQty;
    private Integer scrapQty;
    private Integer status;
    private String statusText;
    private Double progress;

    public Long getDispatchId() {
        return dispatchId;
    }

    public void setDispatchId(Long dispatchId) {
        this.dispatchId = dispatchId;
    }

    public String getDispatchNo() {
        return dispatchNo;
    }

    public void setDispatchNo(String dispatchNo) {
        this.dispatchNo = dispatchNo;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getWorkCenterName() {
        return workCenterName;
    }

    public void setWorkCenterName(String workCenterName) {
        this.workCenterName = workCenterName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getDispatchQty() {
        return dispatchQty;
    }

    public void setDispatchQty(Integer dispatchQty) {
        this.dispatchQty = dispatchQty;
    }

    public Integer getCompletedQty() {
        return completedQty;
    }

    public void setCompletedQty(Integer completedQty) {
        this.completedQty = completedQty;
    }

    public Integer getScrapQty() {
        return scrapQty;
    }

    public void setScrapQty(Integer scrapQty) {
        this.scrapQty = scrapQty;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }
}
