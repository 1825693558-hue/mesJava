package com.demo.mes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("quality_record")
public class QualityRecord extends BaseEntity {
    private Long dispatchId;
    private Long inspectorId;
    private Integer checkType;
    private Integer result;
    private Integer sampleQty;
    private Integer defectQty;
    private String defectDesc;
    private LocalDateTime checkTime;

    public Long getDispatchId() {
        return dispatchId;
    }

    public void setDispatchId(Long dispatchId) {
        this.dispatchId = dispatchId;
    }

    public Long getInspectorId() {
        return inspectorId;
    }

    public void setInspectorId(Long inspectorId) {
        this.inspectorId = inspectorId;
    }

    public Integer getCheckType() {
        return checkType;
    }

    public void setCheckType(Integer checkType) {
        this.checkType = checkType;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Integer getSampleQty() {
        return sampleQty;
    }

    public void setSampleQty(Integer sampleQty) {
        this.sampleQty = sampleQty;
    }

    public Integer getDefectQty() {
        return defectQty;
    }

    public void setDefectQty(Integer defectQty) {
        this.defectQty = defectQty;
    }

    public String getDefectDesc() {
        return defectDesc;
    }

    public void setDefectDesc(String defectDesc) {
        this.defectDesc = defectDesc;
    }

    public LocalDateTime getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(LocalDateTime checkTime) {
        this.checkTime = checkTime;
    }
}
