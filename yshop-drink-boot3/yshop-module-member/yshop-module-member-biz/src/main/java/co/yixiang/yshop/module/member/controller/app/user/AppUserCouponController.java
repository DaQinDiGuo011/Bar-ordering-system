package co.yixiang.yshop.module.member.controller.app.user;

import co.yixiang.yshop.framework.common.pojo.CommonResult;
import co.yixiang.yshop.module.member.controller.app.user.vo.UserCouponVO;
import co.yixiang.yshop.module.member.service.user.UserCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/coupon")
public class AppUserCouponController {

    @Autowired
    private UserCouponService userCouponService;

    @GetMapping("/getUserCouponList")
    public CommonResult<UserCouponVO> getUserCouponList( @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String keyword
    ){
        UserCouponVO data = userCouponService.getUserCouponList(status, type, keyword);
        return CommonResult.success(data);
    }

}
