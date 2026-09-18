package com.demo.mes.vo;

import java.util.List;

public class OrderProgressVO {
    private Long orderId;
    private String orderNo;
    private String productName;
    private Integer plannedQty;
    private Integer completedQty;
    private Integer scrapQty;
    private Double progress;
    private List<DispatchProgressVO> dispatches;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getPlannedQty() {
        return plannedQty;
    }

    public void setPlannedQty(Integer plannedQty) {
        this.plannedQty = plannedQty;
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

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public List<DispatchProgressVO> getDispatches() {
        return dispatches;
    }

    public void setDispatches(List<DispatchProgressVO> dispatches) {
        this.dispatches = dispatches;
    }
}
