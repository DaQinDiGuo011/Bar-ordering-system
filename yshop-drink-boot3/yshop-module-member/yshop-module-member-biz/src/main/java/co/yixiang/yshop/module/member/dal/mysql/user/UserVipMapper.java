package co.yixiang.yshop.module.member.dal.mysql.user;

import co.yixiang.yshop.module.member.dal.dataobject.user.UserVipDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserVipMapper extends BaseMapper<UserVipDO> {

    UserVipDO selectByUserId(@Param("userId") Long userId);
}
