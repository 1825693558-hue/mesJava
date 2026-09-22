package com.demo.mes.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class WorkReportDTO {
    @NotNull(message = "派工单不能为空")
    private Long dispatchId;
    @NotNull(message = "合格数量不能为空")
    @Min(value = 0, message = "合格数量不能为负")
    private Integer goodQty;
    @NotNull(message = "不良数量不能为空")
    @Min(value = 0, message = "不良数量不能为负")
    private Integer scrapQty = 0;
    @NotNull(message = "返修数量不能为空")
    @Min(value = 0, message = "返修数量不能为负")
    private Integer reworkQty = 0;
    private String scrapReason;
    private String remark;

    public Long getDispatchId() {
        return dispatchId;
    }

    public void setDispatchId(Long dispatchId) {
        this.dispatchId = dispatchId;
    }

    public Integer getGoodQty() {
        return goodQty;
    }

    public void setGoodQty(Integer goodQty) {
        this.goodQty = goodQty;
    }

    public Integer getScrapQty() {
        return scrapQty;
    }

    public void setScrapQty(Integer scrapQty) {
        this.scrapQty = scrapQty;
    }

    public Integer getReworkQty() {
        return reworkQty;
    }

    public void setReworkQty(Integer reworkQty) {
        this.reworkQty = reworkQty;
    }

    public String getScrapReason() {
        return scrapReason;
    }

    public void setScrapReason(String scrapReason) {
        this.scrapReason = scrapReason;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
