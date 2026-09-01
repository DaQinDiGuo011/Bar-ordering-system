package co.yixiang.yshop.module.member.dal.dataobject.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName(value = "recharge_order", autoResultMap = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargeOrderDO {

    private Long id;
    private String orderNo;
    private Long userId;
    private Long packageId;
    private BigDecimal rechargeAmount;
    private BigDecimal giftAmount;
    private Integer giftGrowValue;
    private Integer payStatus;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
}
