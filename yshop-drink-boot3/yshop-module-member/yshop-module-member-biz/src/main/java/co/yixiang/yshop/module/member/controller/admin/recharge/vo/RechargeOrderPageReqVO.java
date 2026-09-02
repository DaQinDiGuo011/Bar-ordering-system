package co.yixiang.yshop.module.member.controller.admin.recharge.vo;

import co.yixiang.yshop.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static co.yixiang.yshop.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 充值订单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RechargeOrderPageReqVO extends PageParam {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "支付状态：0-待支付 1-已支付", example = "1")
    private Integer payStatus;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
