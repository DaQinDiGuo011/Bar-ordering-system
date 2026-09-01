package co.yixiang.yshop.module.member.dal.mysql.user;

import co.yixiang.yshop.module.member.dal.dataobject.user.StorePointGoodsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PointGoodsMapper {

    List<StorePointGoodsDO> selectOnlineGoods();
    StorePointGoodsDO selectById(@Param("id") Integer id);

}
