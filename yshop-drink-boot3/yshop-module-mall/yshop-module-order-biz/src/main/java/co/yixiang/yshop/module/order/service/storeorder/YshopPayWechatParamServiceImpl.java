package co.yixiang.yshop.module.order.service.storeorder;

import co.yixiang.yshop.module.order.dal.dataobject.storeorder.YshopPayWechatDO;
import co.yixiang.yshop.module.order.dal.mysql.storeorder.YshopPayWechatParamMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
public class YshopPayWechatParamServiceImpl implements YshopPayWechatParamService{

    @Resource
    private YshopPayWechatParamMapper wechatParamMapper;

    @Override
    public void addInfo(YshopPayWechatDO payWechatDO) {
        wechatParamMapper.insert(payWechatDO);
    }

    @Override
    public YshopPayWechatDO getInfoByOrderId(String orderId) {
        return wechatParamMapper.selectOne("order_id", orderId);
    }
}
