package co.yixiang.yshop.module.order.controller.admin.storeorder.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WineStorePageRespVO {

    private Long id;
    private Long userId;
    private Long productId;
    private String realName;
    private String phone;
    private Integer num;
    private String remark;
    private String storeNo;
    private Integer storeStatus;
    private String storeStatusDesc;
    private LocalDateTime receiveTime;
    private LocalDateTime createTime;
    private Long tenantId;
    // 商品名称，关联查询展示
    private String storeName;
    private String payType;
    private String deskNumber;

    private BigDecimal totalPrice;

    private BigDecimal couponPrice;

    private BigDecimal actualPayPrice;

    private String couponIdList;

}
