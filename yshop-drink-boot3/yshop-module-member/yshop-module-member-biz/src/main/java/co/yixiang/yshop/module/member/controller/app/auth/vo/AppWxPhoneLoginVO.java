package co.yixiang.yshop.module.member.controller.app.auth.vo;

import lombok.Data;

@Data
public class AppWxPhoneLoginVO {

    /**
     * uni.login() 获取的jsCode
     */
    private String jsCode;
    /**
     * getphonenumber 回调拿到的phoneCode
     */
    private String phoneCode;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户头像
     */
    private String avatar;
}
