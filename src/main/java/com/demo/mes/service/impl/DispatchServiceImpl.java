package com.demo.mes.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.mes.common.exception.BusinessException;
import com.demo.mes.entity.Dispatch;
import com.demo.mes.entity.SysUser;
import com.demo.mes.mapper.DispatchMapper;
import com.demo.mes.mapper.SysUserMapper;
import com.demo.mes.service.DispatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DispatchServiceImpl extends ServiceImpl<DispatchMapper, Dispatch>
        implements DispatchService {

    private static final Logger log = LoggerFactory.getLogger(DispatchServiceImpl.class);

    private final SysUserMapper sysUserMapper;

    @Autowired
    public DispatchServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    @Transactional
    public void startDispatch(Long dispatchId, Long operatorId) {
        Dispatch dispatch = baseMapper.selectById(dispatchId);
        if (dispatch == null) {
            throw new BusinessException("派工单不存在");
        }
        if (dispatch.getStatus() != 0 && dispatch.getStatus() != 2) {
            throw new BusinessException("只有待开工或已暂停的派工单才能开始");
        }
        dispatch.setStatus(1);
        dispatch.setOperatorId(operatorId);
        if (dispatch.getActualStartTime() == null) {
            dispatch.setActualStartTime(LocalDateTime.now());
        }
        baseMapper.updateById(dispatch);
    }

    @Override
    @Transactional
    public void pauseDispatch(Long dispatchId) {
        Dispatch dispatch = baseMapper.selectById(dispatchId);
        if (dispatch == null) {
            throw new BusinessException("派工单不存在");
        }
        if (dispatch.getStatus() != 1) {
            throw new BusinessException("只有进行中的派工单才能暂停");
        }
        dispatch.setStatus(2);
        baseMapper.updateById(dispatch);
    }

    @Override
    @Transactional
    public void completeDispatch(Long dispatchId) {
        Dispatch dispatch = baseMapper.selectById(dispatchId);
        if (dispatch == null) {
            throw new BusinessException("派工单不存在");
        }
        if (dispatch.getStatus() == 3) {
            throw new BusinessException("派工单已完成");
        }
        // 校验报工数量，防止零报工直接完成
        int reportedQty = dispatch.getCompletedQty() + dispatch.getScrapQty();
        if (reportedQty <= 0) {
            throw new BusinessException("暂无报工记录，不能完成派工单");
        }
        if (reportedQty < dispatch.getDispatchQty()) {
            throw new BusinessException("报工数量不足（已报" + reportedQty + "/" + dispatch.getDispatchQty() + "），不能完成派工单");
        }
        dispatch.setStatus(3);
        dispatch.setActualEndTime(LocalDateTime.now());
        baseMapper.updateById(dispatch);
    }

    @Override
    @Transactional
    public void assignOperator(Long dispatchId, Long operatorId) {
        Dispatch dispatch = baseMapper.selectById(dispatchId);
        if (dispatch == null) {
            throw new BusinessException("派工单不存在");
        }
        if (dispatch.getStatus() == 3) {
            throw new BusinessException("派工单已完成，不能重新指派");
        }
        if (operatorId != null) {
            SysUser operator = sysUserMapper.selectById(operatorId);
            if (operator == null) {
                throw new BusinessException("操作用户不存在");
            }
        }
        dispatch.setOperatorId(operatorId);
        baseMapper.updateById(dispatch);
    }
}
