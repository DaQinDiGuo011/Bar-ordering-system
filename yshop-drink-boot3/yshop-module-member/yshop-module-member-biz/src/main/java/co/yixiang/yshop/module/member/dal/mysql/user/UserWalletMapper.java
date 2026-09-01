package co.yixiang.yshop.module.member.dal.mysql.user;

import co.yixiang.yshop.module.member.dal.dataobject.user.UserWalletDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface UserWalletMapper {

    int rechargeBalance(@Param("userId") Long userId,
                        @Param("addBalance") BigDecimal addBalance,
                        @Param("addGrow") Integer addGrow,
                        @Param("addTotalRecharge") BigDecimal addTotalRecharge,
                        @Param("addTotalGift") BigDecimal addTotalGift);

    UserWalletDO selectByUserId(@Param("userId") Long userId);

    // 钱包余额增加
    int addBalance(@Param("userId") Long userId, @Param("money") BigDecimal money);
}
