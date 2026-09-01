package co.yixiang.yshop.module.member.controller.app.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppLoginRespVO {

    private Integer errcode;
    private String errmsg;
    private  UserInfo userInfo;

    @Schema(description = "用户编号", required = true, example = "1024")
    private Long userId;

    @Schema(description = "访问令牌", required = true, example = "happy")
    private String accessToken;

    @Schema(description = "刷新令牌", required = true, example = "nice")
    private String refreshToken;

    @Schema(description = "过期时间", required = true)
    private LocalDateTime expiresTime;

    private Boolean isActive;
    @Data
    public static class UserInfo{
        private String phoneNumber;
        private String purePhoneNumber;
        private String countryCode;

        private String realName;
        private String avatar;
        private String nickname;

        private String cardId;

        private String openId;
        /**
         * 用户余额
         */
        private BigDecimal nowMoney;
        /**
         * 用户剩余积分
         */
        private Integer integral;

        /**
         * 优惠券数量
         */
        private Integer couponNum;

        private Long id;

        private Long wineCount;
    }

}
