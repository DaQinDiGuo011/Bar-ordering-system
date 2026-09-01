package co.yixiang.yshop.module.member.controller.app.user.vo;

import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorePointExchangeVO {

    private Integer id;
    private String orderNo;
    private Long userId;
    private Integer goodsId;
    private String goodsName;
    private Integer usePoint;
    /** 0未完成 1已完成 */
    private Integer status;
    private LocalDateTime createdAt;
}
