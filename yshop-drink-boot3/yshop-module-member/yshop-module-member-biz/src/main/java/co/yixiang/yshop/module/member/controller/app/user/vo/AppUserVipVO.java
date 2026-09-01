package co.yixiang.yshop.module.member.controller.app.user.vo;

import lombok.Data;

import java.util.List;

@Data
public class AppUserVipVO {

    private Long growthValue;
    private List<VipLevelConfigVO> vipList;
}
