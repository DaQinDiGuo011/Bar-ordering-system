package co.yixiang.yshop.module.order.service.storeorder;

import co.yixiang.yshop.module.order.dal.dataobject.storeorder.YshopPayWechatDO;

public interface YshopPayWechatParamService {

    void addInfo(YshopPayWechatDO payWechatDO);

    YshopPayWechatDO getInfoByOrderId(String orderId);
}
