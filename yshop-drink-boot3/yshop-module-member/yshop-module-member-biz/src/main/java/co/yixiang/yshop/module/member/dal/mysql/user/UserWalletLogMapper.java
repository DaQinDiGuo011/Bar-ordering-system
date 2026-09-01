package co.yixiang.yshop.module.member.dal.mysql.user;

import co.yixiang.yshop.module.member.dal.dataobject.user.UserWalletLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserWalletLogMapper {
    List<UserWalletLogDO> selectLog(@Param("userId") Long userId, @Param("type") Integer type);
    Map<String, BigDecimal> sumPayMoney(@Param("userId") Long userId);
    int insert(UserWalletLogDO log);
}
