package co.yixiang.yshop.module.member.dal.mysql.user;

import co.yixiang.yshop.module.member.controller.admin.recharge.vo.RechargeOrderPageReqVO;
import co.yixiang.yshop.module.member.dal.dataobject.user.RechargeOrderDO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface RechargeOrderMapper {

    int insert(RechargeOrderDO order);

    RechargeOrderDO selectByOrderNo(@Param("orderNo") String orderNo);

    int updatePaySuccess(@Param("orderNo") String orderNo, @Param("now") LocalDateTime now);

    IPage<RechargeOrderDO> selectPage(Page<RechargeOrderDO> page, @Param("reqVO") RechargeOrderPageReqVO reqVO);

}
