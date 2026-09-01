package co.yixiang.yshop.module.member.controller.app.user.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;

@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopPointsVO implements Serializable {

    private Long id;

    /**
     * 用户nickname
     */
    @NotBlank(message = "用户昵称不能为空")
    private String nickname;

    /**
     * avatar
     */
    @NotBlank(message = "用户头像不能为空")
    private String avatar;
    /**
     * mobile
     */
    private String mobile;

    private Integer integral;

    /** 全局排名 */
    private Integer rankNo;

}
