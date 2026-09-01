package co.yixiang.yshop.module.member.controller.app.user.vo;

import lombok.*;

import java.math.BigDecimal;
@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppRechargePackageVO {
    private Long id;
    private BigDecimal amount;
    private BigDecimal giftAmount;
    private Integer growValue;
    private String vipLevel;
    private Integer sort;
    private Integer status;
}
