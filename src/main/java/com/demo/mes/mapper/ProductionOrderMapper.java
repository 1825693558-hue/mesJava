package com.demo.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.mes.entity.ProductionOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface ProductionOrderMapper extends BaseMapper<ProductionOrder> {

    /**
     * 原子累加订单报工数量。
     */
    @Update("UPDATE production_order SET completed_qty = completed_qty + #{goodQty}, " +
            "scrap_qty = scrap_qty + #{scrapQty}, update_time = NOW() " +
            "WHERE id = #{orderId} AND deleted = 0")
    int addReportQty(@Param("orderId") Long orderId,
                     @Param("goodQty") int goodQty,
                     @Param("scrapQty") int scrapQty);

    /**
     * 更新订单状态及时间字段，startTime 仅在 actual_start_time 为 null 时填充。
     */
    @Update("UPDATE production_order SET status = #{status}, " +
            "actual_start_time = COALESCE(actual_start_time, #{startTime}), " +
            "actual_end_time = #{endTime}, update_time = NOW() " +
            "WHERE id = #{id} AND deleted = 0")
    int updateStatusInfo(@Param("id") Long id,
                         @Param("status") int status,
                         @Param("startTime") LocalDateTime startTime,
                         @Param("endTime") LocalDateTime endTime);
}
