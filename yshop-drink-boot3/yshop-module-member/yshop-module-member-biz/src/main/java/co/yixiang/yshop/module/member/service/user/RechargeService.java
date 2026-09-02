package co.yixiang.yshop.module.member.service.user;

import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.member.controller.admin.recharge.vo.RechargeOrderPageReqVO;
import co.yixiang.yshop.module.member.controller.admin.recharge.vo.RechargePackagePageReqVO;
import co.yixiang.yshop.module.member.controller.admin.recharge.vo.RechargePackageSaveReqVO;
import co.yixiang.yshop.module.member.controller.app.user.vo.AppRechargeOrderVO;
import co.yixiang.yshop.module.member.controller.app.user.vo.AppRechargePackageVO;
import co.yixiang.yshop.module.member.dal.dataobject.user.RechargeOrderDO;
import co.yixiang.yshop.module.member.dal.dataobject.user.RechargePackageDO;

import java.util.List;
import java.util.Map;

public interface RechargeService {

    public List<AppRechargePackageVO> getPackageList();

    public Map<String,Object> createRechargeOrder(AppRechargeOrderVO dto);

    public void handlePaySuccess(String orderNo);

    RechargeOrderDO getOrderByNo(String orderNo);

    PageResult<RechargeOrderDO> getRechargeOrderPage(RechargeOrderPageReqVO reqVO);

    PageResult<RechargePackageDO> getRechargePackagePage(RechargePackagePageReqVO reqVO);

    Long createRechargePackage(RechargePackageSaveReqVO dto);

    void updateRechargePackage(RechargePackageSaveReqVO dto);

}
