package co.yixiang.yshop.module.infra.controller.admin.config.vo;

import lombok.Data;

@Data
public class AccessTokenDTO {

    private String access_token;
    private Integer expires_in;
    private String errcode;
    private String errmsg;
}
