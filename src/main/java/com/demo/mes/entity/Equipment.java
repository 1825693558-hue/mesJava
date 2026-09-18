package com.demo.mes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;

@TableName("equipment")
public class Equipment extends BaseEntity {
    private String equipmentCode;
    private String equipmentName;
    private Integer equipmentType;
    private Long workCenterId;
    private Integer status;
    private LocalDate purchaseDate;

    public String getEquipmentCode() {
        return equipmentCode;
    }

    public void setEquipmentCode(String equipmentCode) {
        this.equipmentCode = equipmentCode;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public Integer getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(Integer equipmentType) {
        this.equipmentType = equipmentType;
    }

    public Long getWorkCenterId() {
        return workCenterId;
    }

    public void setWorkCenterId(Long workCenterId) {
        this.workCenterId = workCenterId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
