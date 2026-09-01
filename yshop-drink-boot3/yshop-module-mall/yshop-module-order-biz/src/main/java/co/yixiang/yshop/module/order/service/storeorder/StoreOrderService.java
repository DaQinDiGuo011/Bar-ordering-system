package co.yixiang.yshop.module.order.service.storeorder;

import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.order.controller.admin.storeorder.vo.*;
import co.yixiang.yshop.module.order.dal.dataobject.storeorder.StoreOrderDO;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * 订单 Service 接口
 *
 * @author yshop
 */
public interface StoreOrderService {

    /**
     * 创建订单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createStoreOrder(@Valid StoreOrderCreateReqVO createReqVO);

    /**
     * 更新订单
     *
     * @param updateReqVO 更新信息
     */
    void updateStoreOrder(@Valid StoreOrderUpdateReqVO updateReqVO);

    /**
     * 删除订单
     *
     * @param id 编号
     */
    void deleteStoreOrder(Long id);

    /**
     * 订单线下支付
     *
     * @param id 编号
     */
    void payStoreOrder(Long id);

    /**
     * 确认收货
     *
     * @param id 编号
     */
    void takeStoreOrder(Long id);

    /**
     * 制作中
     * @param id
     */
    void orderConfirm(Long id);

    /**
     * 配送中
     */
    void pendingReceipt(Long id);
    /**
     * 获得订单
     *
     * @param id 编号
     * @return 订单
     */
    StoreOrderRespVO getStoreOrder(Long id);

    /**
     * 获得订单列表
     *
     * @param ids 编号
     * @return 订单列表
     */
    List<StoreOrderDO> getStoreOrderList(Collection<Long> ids);

    /**
     * 获得订单分页
     *
     * @param pageReqVO 分页查询
     * @return 订单分页
     */
    PageResult<StoreOrderRespVO> getStoreOrderPage(StoreOrderPageReqVO pageReqVO);

    PageResult<StoreOrderRespVO> getWorkStoreOrderPage(StoreOrderPageReqVO pageReqVO);

    /**
     * 获得订单列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 订单列表
     */
    List<StoreOrderDO> getStoreOrderList(StoreOrderExportReqVO exportReqVO);

    /**
     * 确认订单退款
     *
     * @param id 单号
     * @param price   金额
     * @param type    ShopCommonEnum
     * @param salesId 售后id
     */
    void orderRefund(Long id, BigDecimal price, Integer type, Long salesId);

    void reRefund(Long id, String msg, Long uid);
    /**
     * 订单30s内通知
     * @return
     */
    Long orderNotice();


    PageResult<DailyTurnoverRespVO> getDailyTurnoverPage(DailyTurnoverPageReqVO reqVO);

}
