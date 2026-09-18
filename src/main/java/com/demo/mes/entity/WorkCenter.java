package com.demo.mes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;

@TableName("work_center")
public class WorkCenter extends BaseEntity {
    private String centerCode;
    private String centerName;
    private Integer centerType;
    private BigDecimal capacityPerHour;
    private Integer status;

    public String getCenterCode() {
        return centerCode;
    }

    public void setCenterCode(String centerCode) {
        this.centerCode = centerCode;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public Integer getCenterType() {
        return centerType;
    }

    public void setCenterType(Integer centerType) {
        this.centerType = centerType;
    }

    public BigDecimal getCapacityPerHour() {
        return capacityPerHour;
    }

    public void setCapacityPerHour(BigDecimal capacityPerHour) {
        this.capacityPerHour = capacityPerHour;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
