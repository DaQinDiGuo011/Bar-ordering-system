package co.yixiang.yshop.module.member.service.user;

import co.yixiang.yshop.module.member.controller.app.user.vo.UserCouponVO;

public interface UserCouponService {

    UserCouponVO getUserCouponList(Integer status, Integer type, String keyword);

}
