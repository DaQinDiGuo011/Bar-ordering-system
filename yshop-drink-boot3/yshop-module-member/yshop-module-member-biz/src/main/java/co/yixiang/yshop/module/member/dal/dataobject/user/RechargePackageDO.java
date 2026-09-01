package co.yixiang.yshop.module.member.dal.dataobject.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@TableName(value = "recharge_package", autoResultMap = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargePackageDO {

    private Long id;
    private BigDecimal amount;
    private BigDecimal giftAmount;
    private Integer growValue;
    private String vipLevel;
    private Integer sort;
    private Integer status;
}
