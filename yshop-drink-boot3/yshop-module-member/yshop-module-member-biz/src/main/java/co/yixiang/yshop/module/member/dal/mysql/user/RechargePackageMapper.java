package co.yixiang.yshop.module.member.dal.mysql.user;

import co.yixiang.yshop.module.member.dal.dataobject.user.RechargePackageDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RechargePackageMapper {

    RechargePackageDO selectById(@Param("id") Long id);
    List<RechargePackageDO> selectEnableList();
}
