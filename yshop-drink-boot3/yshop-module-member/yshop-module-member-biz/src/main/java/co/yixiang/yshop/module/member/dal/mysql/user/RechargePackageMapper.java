package co.yixiang.yshop.module.member.dal.mysql.user;

import co.yixiang.yshop.module.member.controller.admin.recharge.vo.RechargePackagePageReqVO;
import co.yixiang.yshop.module.member.dal.dataobject.user.RechargePackageDO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RechargePackageMapper {

    RechargePackageDO selectById(@Param("id") Long id);

    List<RechargePackageDO> selectEnableList();

    IPage<RechargePackageDO> selectPage(Page<RechargePackageDO> page, @Param("reqVO") RechargePackagePageReqVO reqVO);

    int insert(RechargePackageDO pkg);

    int update(RechargePackageDO pkg);

}
