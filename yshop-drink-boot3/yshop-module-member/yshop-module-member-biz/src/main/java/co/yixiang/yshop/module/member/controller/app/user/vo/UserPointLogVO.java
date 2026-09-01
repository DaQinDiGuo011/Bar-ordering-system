package co.yixiang.yshop.module.member.controller.app.user.vo;

import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPointLogVO {

    private Integer id;
    private Long userId;
    /** 1收入 2消耗 */
    private Integer type;
    private Integer point;
    private String remark;
    private LocalDateTime createdAt;
}
