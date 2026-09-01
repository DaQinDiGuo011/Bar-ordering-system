package co.yixiang.yshop.module.order.dal.mysql.storeorder;

import co.yixiang.yshop.module.order.dal.dataobject.storeorder.BarTableDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BarTableMapper{
    List<BarTableDO> selectAll();
    BarTableDO selectById(Long id);
    // 查询指定日期已经被预定的桌台id
    List<Long> selectReservedTableId(@Param("date") String date);
}