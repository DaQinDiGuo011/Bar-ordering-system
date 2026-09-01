package co.yixiang.yshop.module.order.controller.admin.storeorder.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WineStoreSaveReqVO {

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

    private String pwd;
}
