package co.yixiang.yshop.module.member.dal.mysql.user;

import co.yixiang.yshop.module.member.dal.dataobject.user.VipLevelConfigDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VipLevelConfigMapper {
    List<VipLevelConfigDO> selectAllLevel();
}
