package co.yixiang.yshop.module.order.controller.admin.storeorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "每日营业额统计返回VO")
public class DailyTurnoverRespVO {

    @Schema(description = "营业统计日期 yyyy‑MM‑dd")
    private String bizDate;

    @Schema(description = "订单总数量(已支付)")
    private Integer payOrderCount;

    @Schema(description = "总营业额(实际支付pay_price)")
    private BigDecimal totalTurnover;

    @Schema(description = "微信支付金额")
    private BigDecimal wxAmount;

    @Schema(description = "支付宝支付金额")
    private BigDecimal aliAmount;

    @Schema(description = "余额支付金额")
    private BigDecimal yueAmount;

    @Schema(description = "退款总金额")
    private BigDecimal refundAmount;

    @Schema(description = "统计区间展示文本，例：2026‑09‑01 08:00 ~ 2026‑09‑02 07:59")
    private String timeRangeStr;

}
