package co.yixiang.yshop.module.order.controller.admin.storeorder.vo;

import lombok.Data;

@Data
public class WineStorePageReqVO {

    private Integer pageNo;
    private Integer pageSize;
    private String realName;
    private String phone;
    private String storeNo;
    /**
     * 等于0则查1、2、3，1、2、3查对应的状态1、2、3，4查4和5，5查4，6查5
     */
    private Integer storeStatus;
    private Long userId;
}
