package co.yixiang.yshop.module.order.controller.admin.storeorder.vo;

import lombok.Data;

@Data
public class WineStoreReqVO {

    private Long id;
    private Integer storeStatus;
    private Integer number;
    private Long productId;
}
