package com.demo.mes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.mes.dto.WorkReportDTO;
import com.demo.mes.entity.WorkReport;
import com.demo.mes.vo.WorkReportResultVO;

public interface WorkReportService extends IService<WorkReport> {
    WorkReportResultVO submitReport(WorkReportDTO dto, Long operatorId);
}
