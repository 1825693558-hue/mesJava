package com.demo.mes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("work_report")
public class WorkReport extends BaseEntity {
    private Long dispatchId;
    private Long operatorId;
    private Integer goodQty;
    private Integer scrapQty;
    private Integer reworkQty;
    private String scrapReason;
    private LocalDateTime reportTime;
    private String remark;

    public Long getDispatchId() {
        return dispatchId;
    }

    public void setDispatchId(Long dispatchId) {
        this.dispatchId = dispatchId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
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

    public LocalDateTime getReportTime() {
        return reportTime;
    }

    public void setReportTime(LocalDateTime reportTime) {
        this.reportTime = reportTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
