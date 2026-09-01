package co.yixiang.yshop.module.order.dal.mysql.storeorder;

import co.yixiang.yshop.module.order.dal.dataobject.storeorder.ReserveOrderDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReserveOrderMapper{

    int insert(ReserveOrderDO order);
    List<ReserveOrderDO> selectUserOrder(@Param("userId") Long userId);
    ReserveOrderDO selectById(Long id);
    int updateStatus(@Param("id") Long id,@Param("status") Integer status);

}
