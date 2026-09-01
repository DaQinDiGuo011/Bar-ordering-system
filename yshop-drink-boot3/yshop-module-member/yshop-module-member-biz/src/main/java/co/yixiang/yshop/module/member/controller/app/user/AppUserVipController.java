package co.yixiang.yshop.module.member.controller.app.user;

import co.yixiang.yshop.framework.common.pojo.CommonResult;
import co.yixiang.yshop.module.member.controller.app.user.vo.AppOpenVipVO;
import co.yixiang.yshop.module.member.controller.app.user.vo.AppUserVipVO;
import co.yixiang.yshop.module.member.service.user.UserVipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/vip")
public class AppUserVipController {

    @Autowired
    private UserVipService userVipService;

    /**
     * 获取会员信息
     */
    @GetMapping("/getVipInfo")
    public CommonResult<AppUserVipVO> getVipInfo(){
        return CommonResult.success(userVipService.getVipInfo());
    }

    /**
     * 开通会员卡
     */
    @PostMapping("/openVipCard")
    public CommonResult<String> openVipCard( @RequestBody AppOpenVipVO dto){
        userVipService.openVipCard(dto);
        return CommonResult.success("");
    }
}
