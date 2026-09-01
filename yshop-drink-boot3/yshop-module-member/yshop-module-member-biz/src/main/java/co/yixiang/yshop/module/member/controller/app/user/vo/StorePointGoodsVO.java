package co.yixiang.yshop.module.member.controller.app.user.vo;

import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorePointGoodsVO {

    private Integer id;
    private String name;
    private String image;
    private Integer needPoint;
    private Integer stock;
    private Integer status;
    private LocalDateTime createdAt;
}
