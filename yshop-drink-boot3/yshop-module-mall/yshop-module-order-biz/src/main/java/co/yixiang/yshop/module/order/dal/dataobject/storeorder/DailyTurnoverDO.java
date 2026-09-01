package co.yixiang.yshop.module.order.dal.dataobject.storeorder;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DailyTurnoverDO {
    /**
     *营业统计日期 yyyy‑MM‑dd
     */
    private String bizDate;
    /**
     *订单总数量(已支付)
     */
    private Integer payOrderCount;
    /**
     * 总营业额(实际支付pay_price)
     */
    private BigDecimal totalTurnover;
    /**
     * 微信支付金额
     */
    private BigDecimal wxAmount;
    /**
     * 余额支付金额
     */
    private BigDecimal yueAmount;

    /**
     * 退款总金额
     */
    private BigDecimal refundAmount;
    /**
     * 统计区间展示文本，例：2026‑09‑01 08:00 ~ 2026‑09‑02 07:59
     */
    private String timeRangeStr;

}
