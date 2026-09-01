package co.yixiang.yshop.module.member.dal.dataobject.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName(value = "user_wallet", autoResultMap = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWalletDO {

    private Long id;
    private Long userId;
    private BigDecimal balance;
    private BigDecimal totalRecharge;
    private BigDecimal totalGift;
    private Integer growValue;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
