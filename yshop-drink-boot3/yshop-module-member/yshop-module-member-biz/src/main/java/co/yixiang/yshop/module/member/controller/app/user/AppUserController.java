package co.yixiang.yshop.module.member.controller.app.user;

import co.yixiang.yshop.framework.common.pojo.CommonResult;
import co.yixiang.yshop.framework.security.core.annotations.PreAuthenticated;
import co.yixiang.yshop.module.member.controller.app.user.vo.*;
import co.yixiang.yshop.module.member.convert.user.UserConvert;
import co.yixiang.yshop.module.member.dal.dataobject.user.MemberUserDO;
import co.yixiang.yshop.module.member.service.user.MemberUserService;
import co.yixiang.yshop.module.member.service.user.RechargeService;
import co.yixiang.yshop.module.member.service.userbill.UserBillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static co.yixiang.yshop.framework.common.exception.util.ServiceExceptionUtil.exception;
import static co.yixiang.yshop.framework.common.pojo.CommonResult.success;
import static co.yixiang.yshop.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static co.yixiang.yshop.module.infra.enums.ErrorCodeConstants.FILE_IS_EMPTY;

@Tag(name = "用户 APP - 用户个人中心")
@RestController
@RequestMapping("/member/user")
@Validated
@Slf4j
public class AppUserController {

    @Resource
    private MemberUserService userService;
    @Resource
    private UserBillService userBillService;

    @Resource
    private RechargeService rechargeService;


    @PostMapping("/update-nickname")
    @Operation(summary = "修改用户昵称或者生日")
    @Parameters({
            @Parameter(name = "nickname", description = "用户昵称",
                    required = true, example = "wang"),
            @Parameter(name = "birthday", description = "生日",
                    required = true, example = "2023-11-12"),
            @Parameter(name = "gender", description = "性别",
                    required = true, example = "o"),
            @Parameter(name = "avatar", description = "用户头像",
                    required = true, example = ""),
            @Parameter(name = "mobile", description = "手机",
                    required = true, example = "")
    })
    @PreAuthenticated
    public CommonResult<Boolean> updateUserNickname(@RequestBody @Valid AppUserNickVO appUserNickVO) {
        userService.updateUserNickname(getLoginUserId(), appUserNickVO.getNickname(),appUserNickVO.getBirthday(),
                appUserNickVO.getGender(),appUserNickVO.getAvatar(),appUserNickVO.getMobile());
        return success(true);
    }

    @PostMapping("/update-avatar")
    @Operation(summary = "修改用户头像")
    @PreAuthenticated
    public CommonResult<String> updateUserAvatar(@RequestParam("avatarFile") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw exception(FILE_IS_EMPTY);
        }
        String avatar = userService.updateUserAvatar(getLoginUserId(), file.getInputStream());
        return success(avatar);
    }

    @GetMapping("/get")
    @Operation(summary = "获得基本信息")
    @PreAuthenticated
    public CommonResult<AppUserInfoRespVO> getUserInfo() {
        MemberUserDO user = userService.getUser(getLoginUserId());
        return success(UserConvert.INSTANCE.convert(user));
    }

    @PostMapping("/update-mobile")
    @Operation(summary = "修改用户手机")
    @PreAuthenticated
    public CommonResult<Boolean> updateMobile(@RequestBody @Valid AppUserUpdateMobileReqVO reqVO) {
        userService.updateUserMobile(getLoginUserId(), reqVO);
        return success(true);
    }

    @GetMapping("/get-info")
    @Operation(summary = "获得基本信息")
    @PreAuthenticated
    public CommonResult<AppUserQueryVo> getUserBaseInfo() {

        return success(userService.getAppUser(getLoginUserId()));
    }

    @GetMapping("/getBill")
    @Operation(summary = "获取用户账单")
    @PreAuthenticated
    @Parameters({
            @Parameter(name = "cate", description = "状态,0余额 1-积分",
                    required = true, example = "1"),
            @Parameter(name = "type", description = "状态,0全部  1 - 消费 2 - 充值 和 退款",
                    required = true, example = "1"),
            @Parameter(name = "page", description = "页码,默认为1",
                    required = true, example = "1"),
            @Parameter(name = "pagesize", description = "页大小,默认为10",
                    required = true, example = "10      ")
    })
    public CommonResult<List<AppUserBillVO>> getUserBill(@RequestParam(value = "cate", defaultValue = "0") int cate,
                                                         @RequestParam(value = "type", defaultValue = "0") int type,
                                                         @RequestParam(value = "page", defaultValue = "1") int page,
                                                         @RequestParam(value = "pagesize", defaultValue = "10") int pagesize) {
        return success(userBillService.getBillList(getLoginUserId(),cate,type,page,pagesize));
    }


    @GetMapping("/package/list")
    public CommonResult<List<AppRechargePackageVO>> getPackageList() {
        List<AppRechargePackageVO> list = rechargeService.getPackageList();
        return success(list);
    }

    /**
     * 创建充值订单
     */
    @PostMapping("/order/create")
    public CommonResult<Map<String,Object>> createOrder(@RequestBody AppRechargeOrderVO dto) {
        try {
            Map<String,Object> data = rechargeService.createRechargeOrder(dto);
            return success(data);
        }catch (Exception e){
            Map<String,Object> result = new HashMap<>();
            result.put("code", "500");
            result.put("msg", e.getMessage());
            return success(result);
        }
    }

    /**
     * 支付异步回调地址（微信/支付宝）
     */
    @PostMapping("/order/payNotify")
    public CommonResult<String> payNotify(@RequestParam String orderNo) {

        try {
            rechargeService.handlePaySuccess(orderNo);
            return success("success");
        }catch (Exception e){
            return success("fail");
        }
    }

    /**
     * @return
     */
    @GetMapping("/points/rank")
    @Operation(summary = "积分排行分页")
    public CommonResult<List<TopPointsVO>> getPiontPage() {

        return success(userService.getTop50Rank());
    }

}

