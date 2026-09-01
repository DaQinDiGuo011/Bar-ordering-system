package co.yixiang.yshop.module.order.dal.dataobject.storeorder;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("yshop_wine_store")
public class WineStoreDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long productId;

    private String realName;

    private String phone;

    private Integer num;

    private String remark;

    private String storeNo;

    private Integer storeStatus;

    private LocalDateTime receiveTime;

    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Integer deleted;

    private Long tenantId;

    @TableField(value = "spec")
    private String spec;

    private BigDecimal totalPrice;

    private BigDecimal couponPrice;

    private BigDecimal actualPayPrice;

    private String couponIdList;

    private String payType;

}
