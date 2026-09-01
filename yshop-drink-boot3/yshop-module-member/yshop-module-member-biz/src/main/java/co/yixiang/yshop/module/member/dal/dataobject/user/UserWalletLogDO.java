package co.yixiang.yshop.module.member.dal.dataobject.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWalletLogDO {

    private Long id;
    private Long userId;
    /** 1收入 2支出 */
    private Integer type;
    private BigDecimal money;
    private String remark;
    private LocalDateTime createdAt;
}
