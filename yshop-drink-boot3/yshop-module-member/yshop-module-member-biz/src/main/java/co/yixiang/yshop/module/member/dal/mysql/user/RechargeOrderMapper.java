package co.yixiang.yshop.module.member.dal.mysql.user;

import co.yixiang.yshop.module.member.dal.dataobject.user.RechargeOrderDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface RechargeOrderMapper {

    int insert(RechargeOrderDO order);
    RechargeOrderDO selectByOrderNo(@Param("orderNo") String orderNo);
    int updatePaySuccess(@Param("orderNo") String orderNo, @Param("now") LocalDateTime now);
}
