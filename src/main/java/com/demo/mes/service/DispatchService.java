package com.demo.mes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.mes.entity.Dispatch;

public interface DispatchService extends IService<Dispatch> {
    void startDispatch(Long dispatchId, Long operatorId);
    void pauseDispatch(Long dispatchId);
    void completeDispatch(Long dispatchId);
    void assignOperator(Long dispatchId, Long operatorId);
}
