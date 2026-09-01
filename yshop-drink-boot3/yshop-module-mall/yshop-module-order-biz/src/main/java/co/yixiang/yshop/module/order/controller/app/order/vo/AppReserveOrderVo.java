package co.yixiang.yshop.module.order.controller.app.order.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppReserveOrderVo {

    private Long id;
    private Long userId;
    private Long tableId;
    private String tableName;
    private String reserveDate;
    private String reserveTime;
    private String name;
    private String phone;
    private Integer peopleNum;
    private String remark;
    private Integer status; //1待使用 2已取消
    private LocalDateTime createTime;
}
