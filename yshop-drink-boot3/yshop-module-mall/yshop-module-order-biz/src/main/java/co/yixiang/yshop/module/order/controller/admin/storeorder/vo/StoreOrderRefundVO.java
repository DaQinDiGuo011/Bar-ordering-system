package co.yixiang.yshop.module.order.controller.admin.storeorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 订单退款 Request VO")
@Data
@ToString(callSuper = true)
public class StoreOrderRefundVO {

    @Schema(description = "订单ID", required = true, example = "31716")
    @NotNull(message = "订单ID不能为空")
    private Long id;

    @Schema(description = "退款金额", required = true, example = "31716")
//    @NotNull(message = "退款金额不能为空")
    private BigDecimal payPrice;

    private String pwd;

    private String refuseReason;

}
