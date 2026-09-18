package com.demo.mes.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ProductionOrderDTO {
    @NotNull(message = "产品不能为空")
    private Long productId;
    @NotNull(message = "计划数量不能为空")
    @Min(value = 1, message = "计划数量必须大于0")
    private Integer plannedQty;
    private Integer priority = 3;
    private LocalDateTime plannedStartTime;
    private LocalDateTime plannedEndTime;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getPlannedQty() {
        return plannedQty;
    }

    public void setPlannedQty(Integer plannedQty) {
        this.plannedQty = plannedQty;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDateTime getPlannedStartTime() {
        return plannedStartTime;
    }

    public void setPlannedStartTime(LocalDateTime plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }

    public LocalDateTime getPlannedEndTime() {
        return plannedEndTime;
    }

    public void setPlannedEndTime(LocalDateTime plannedEndTime) {
        this.plannedEndTime = plannedEndTime;
    }
}
