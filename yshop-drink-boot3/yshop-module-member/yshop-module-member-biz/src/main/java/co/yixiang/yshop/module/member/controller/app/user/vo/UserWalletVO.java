package co.yixiang.yshop.module.member.controller.app.user.vo;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWalletVO {

    private Long id;
    private Long userId;
    private BigDecimal balance;
    private BigDecimal totalRecharge;
    private BigDecimal totalGift;
    private Integer growValue;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
