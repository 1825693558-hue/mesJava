package com.demo.mes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;

@TableName("bom")
public class Bom extends BaseEntity {
    private Long parentMaterialId;
    private Long childMaterialId;
    private BigDecimal quantity;
    private String unit;

    public Long getParentMaterialId() {
        return parentMaterialId;
    }

    public void setParentMaterialId(Long parentMaterialId) {
        this.parentMaterialId = parentMaterialId;
    }

    public Long getChildMaterialId() {
        return childMaterialId;
    }

    public void setChildMaterialId(Long childMaterialId) {
        this.childMaterialId = childMaterialId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
