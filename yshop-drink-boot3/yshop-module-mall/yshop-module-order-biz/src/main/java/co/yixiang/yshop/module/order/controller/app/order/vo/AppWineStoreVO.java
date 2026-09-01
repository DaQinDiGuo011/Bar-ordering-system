package co.yixiang.yshop.module.order.controller.app.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppWineStoreVO {

    private Long id;
    private String storeNo;
    private Long productId;
    private String realName;
    private String phone;
    private Integer num;
    private String remark;
    private Integer storeStatus;
    private LocalDateTime createTime;
    private LocalDateTime receiveTime;

    // 商品信息 关联product
    private String storeName;
    private String image;

    private String deskNumber;

    private String spec;

    private Long shopId;
    private String shopName;

    private Long userId;

    private String payType;

    private BigDecimal totalPrice;

    private BigDecimal couponPrice;

    private BigDecimal actualPayPrice;

    private String couponIdList;
}
