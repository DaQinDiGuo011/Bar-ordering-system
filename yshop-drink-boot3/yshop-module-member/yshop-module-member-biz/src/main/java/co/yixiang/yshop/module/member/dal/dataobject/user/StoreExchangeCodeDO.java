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
public class StoreExchangeCodeDO {

    private Integer id;
    private String code;
    private BigDecimal money;
    /** 0未使用 1已使用 */
    private Integer status;
    private Integer useUserId;
    private LocalDateTime usedAt;
    private LocalDateTime createdAt;
}
