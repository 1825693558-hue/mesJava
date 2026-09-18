package com.demo.mes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;

@TableName("process_step")
public class ProcessStep extends BaseEntity {
    private Long routeId;
    private Integer stepNo;
    private String stepName;
    private Long workCenterId;
    private BigDecimal standardTime;
    private Integer needQc;
    private String description;

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Integer getStepNo() {
        return stepNo;
    }

    public void setStepNo(Integer stepNo) {
        this.stepNo = stepNo;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public Long getWorkCenterId() {
        return workCenterId;
    }

    public void setWorkCenterId(Long workCenterId) {
        this.workCenterId = workCenterId;
    }

    public BigDecimal getStandardTime() {
        return standardTime;
    }

    public void setStandardTime(BigDecimal standardTime) {
        this.standardTime = standardTime;
    }

    public Integer getNeedQc() {
        return needQc;
    }

    public void setNeedQc(Integer needQc) {
        this.needQc = needQc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
