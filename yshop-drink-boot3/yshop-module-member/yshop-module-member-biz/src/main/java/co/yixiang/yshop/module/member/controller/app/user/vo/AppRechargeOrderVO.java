package co.yixiang.yshop.module.member.controller.app.user.vo;

import lombok.*;

import java.math.BigDecimal;
@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppRechargeOrderVO {

    private Long packageId;
    private BigDecimal rechargeAmount;
    private String openId;
}
