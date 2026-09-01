package co.yixiang.yshop.module.member.controller.app.user.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VipLevelConfigVO {

    private Long id;
    private Integer level;
    private String levelName;
    private Long needGrowth;
    private String color;
    private LocalDateTime createTime;
}
