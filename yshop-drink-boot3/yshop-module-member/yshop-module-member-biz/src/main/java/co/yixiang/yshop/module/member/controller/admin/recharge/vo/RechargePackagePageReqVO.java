package co.yixiang.yshop.module.member.controller.admin.recharge.vo;

import co.yixiang.yshop.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 充值套餐分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RechargePackagePageReqVO extends PageParam {

    @Schema(description = "会员等级")
    private String vipLevel;

    @Schema(description = "状态：0-禁用 1-启用", example = "1")
    private Integer status;

}
