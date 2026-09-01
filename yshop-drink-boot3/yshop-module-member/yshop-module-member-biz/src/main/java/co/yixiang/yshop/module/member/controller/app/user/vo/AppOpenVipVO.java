package co.yixiang.yshop.module.member.controller.app.user.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AppOpenVipVO {

    private String name;
    private String phone;
    private Integer gender;
    private LocalDate birthday;
}
