package co.yixiang.yshop.module.member.dal.mysql.user;

import co.yixiang.yshop.module.member.dal.dataobject.user.StoreExchangeCodeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ExchangeCodeMapper {
    StoreExchangeCodeDO selectByCode(@Param("code") String code);
    int updateUsed(@Param("code") String code, @Param("userId") Long userId);
}
