package co.yixiang.yshop.module.order.controller.app.order.param;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppWineStoreParam {

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

    private String creator;

    private LocalDateTime createTime;

    private String updater;

    private LocalDateTime updateTime;

    private Integer deleted;

    private Long tenantId;

    private String spec;

    private BigDecimal totalPrice;

    private BigDecimal couponPrice;

    private BigDecimal actualPayPrice;

    private String couponIdList;
    private String payType;
}
