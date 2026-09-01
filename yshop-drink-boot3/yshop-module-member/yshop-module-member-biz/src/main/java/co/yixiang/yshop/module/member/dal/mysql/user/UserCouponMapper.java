package co.yixiang.yshop.module.member.dal.mysql.user;


import co.yixiang.yshop.module.member.dal.dataobject.user.UserCouponDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserCouponMapper {

    // 分页查询用户卡券
    List<UserCouponDO> selectCouponList(
            @Param("userId") Long userId,
            @Param("status") Integer status,
            @Param("type") Integer type,
            @Param("keyword") String keyword);
    // 统计顶部tab数量
    List<Map<String,Object>> countTopTab(@Param("userId") Long userId);
    // 统计左侧分类数量
    List<Map<String,Object>> countCateTab(@Param("userId") Long userId);

}
