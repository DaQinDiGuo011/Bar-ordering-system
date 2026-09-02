package co.yixiang.yshop.module.member.controller.admin.recharge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 充值套餐新增/修改 Request VO")
@Data
public class RechargePackageSaveReqVO {

    @Schema(description = "套餐编号，新增时为空")
    private Long id;

    @Schema(description = "充值金额", required = true, example = "100")
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额必须大于0")
    private BigDecimal amount;

    @Schema(description = "赠送金额", example = "10")
    private BigDecimal giftAmount;

    @Schema(description = "赠送成长值", example = "100")
    private Integer growValue;

    @Schema(description = "会员等级")
    private String vipLevel;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "状态：0-禁用 1-启用", example = "1")
    private Integer status;

    @Schema(description = "权限密码")
    private String pwd;

}
