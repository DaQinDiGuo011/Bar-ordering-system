package co.yixiang.yshop.module.member.service.user;

import co.yixiang.yshop.module.member.controller.app.user.vo.AppRechargeOrderVO;
import co.yixiang.yshop.module.member.controller.app.user.vo.AppRechargePackageVO;
import co.yixiang.yshop.module.member.dal.dataobject.user.RechargeOrderDO;

import java.util.List;
import java.util.Map;

public interface RechargeService {

    public List<AppRechargePackageVO> getPackageList();

    public Map<String,Object> createRechargeOrder(AppRechargeOrderVO dto);

    public void handlePaySuccess(String orderNo);

    RechargeOrderDO getOrderByNo(String orderNo);



}
