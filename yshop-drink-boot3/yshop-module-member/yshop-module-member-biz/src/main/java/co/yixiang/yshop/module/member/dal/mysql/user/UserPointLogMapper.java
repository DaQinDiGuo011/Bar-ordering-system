package co.yixiang.yshop.module.member.dal.mysql.user;

import co.yixiang.yshop.module.member.dal.dataobject.user.UserPointLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserPointLogMapper {

    List<UserPointLogDO> selectLog(@Param("userId") Long userId);
    int insert(UserPointLogDO log);

}
