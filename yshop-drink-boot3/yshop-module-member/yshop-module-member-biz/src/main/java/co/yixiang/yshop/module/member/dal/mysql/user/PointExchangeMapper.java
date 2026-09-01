package co.yixiang.yshop.module.member.dal.mysql.user;

import co.yixiang.yshop.module.member.dal.dataobject.user.StorePointExchangeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PointExchangeMapper {

    List<StorePointExchangeDO> selectUserExchange(@Param("userId") Long userId, @Param("status") Integer status);
    int insert(StorePointExchangeDO exchange);

}
