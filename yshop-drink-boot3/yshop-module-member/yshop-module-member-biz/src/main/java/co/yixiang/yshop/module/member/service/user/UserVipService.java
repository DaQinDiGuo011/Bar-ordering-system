package co.yixiang.yshop.module.member.service.user;

import co.yixiang.yshop.module.member.controller.app.user.vo.AppOpenVipVO;
import co.yixiang.yshop.module.member.controller.app.user.vo.AppUserVipVO;

public interface UserVipService {

    public AppUserVipVO getVipInfo();


    public void openVipCard(AppOpenVipVO dto);
}
