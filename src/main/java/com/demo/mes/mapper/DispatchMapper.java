package com.demo.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.mes.entity.Dispatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface DispatchMapper extends BaseMapper<Dispatch> {

    /**
     * 原子累加报工数量，WHERE 条件保证不超派工数量（防并发超量）。
     * @return 影响行数，0 表示超量或记录不存在
     */
    @Update("UPDATE dispatch SET completed_qty = completed_qty + #{goodQty}, " +
            "scrap_qty = scrap_qty + #{scrapQty}, update_time = NOW() " +
            "WHERE id = #{dispatchId} AND deleted = 0 " +
            "AND completed_qty + scrap_qty + #{goodQty} + #{scrapQty} <= dispatch_qty")
    int addReportQty(@Param("dispatchId") Long dispatchId,
                     @Param("goodQty") int goodQty,
                     @Param("scrapQty") int scrapQty);

    /**
     * 更新派工单状态及时间字段，startTime 仅在 actual_start_time 为 null 时填充。
     */
    @Update("UPDATE dispatch SET status = #{status}, " +
            "actual_start_time = COALESCE(actual_start_time, #{startTime}), " +
            "actual_end_time = #{endTime}, update_time = NOW() " +
            "WHERE id = #{id} AND deleted = 0")
    int updateStatusInfo(@Param("id") Long id,
                         @Param("status") int status,
                         @Param("startTime") LocalDateTime startTime,
                         @Param("endTime") LocalDateTime endTime);
}
