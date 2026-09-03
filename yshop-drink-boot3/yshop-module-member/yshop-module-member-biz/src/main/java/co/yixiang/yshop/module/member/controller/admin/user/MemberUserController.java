package co.yixiang.yshop.module.member.controller.admin.user;

import co.yixiang.yshop.framework.common.pojo.CommonResult;
import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.coupon.service.couponuser.CouponUserService;
import co.yixiang.yshop.module.member.controller.admin.user.vo.*;
import co.yixiang.yshop.module.member.controller.app.user.vo.TopPointsVO;
import co.yixiang.yshop.module.member.convert.user.UserConvert;
import co.yixiang.yshop.module.member.dal.dataobject.user.MemberUserDO;
import co.yixiang.yshop.module.member.service.user.MemberUserService;
import co.yixiang.yshop.module.member.service.user.UserService;
import co.yixiang.yshop.module.system.controller.admin.permission.vo.permission.SysPasswordConfigVO;
import co.yixiang.yshop.module.system.service.permission.SysPasswordConfigServiceImpl;
import co.yixiang.yshop.module.system.util.oauth2.BusiPwdEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

import static co.yixiang.yshop.framework.common.exception.util.ServiceExceptionUtil.exception;
import static co.yixiang.yshop.framework.common.pojo.CommonResult.success;
import static co.yixiang.yshop.module.system.enums.ErrorCodeConstants.PWD_ERROR;

@Tag(name = "管理后台 - 用户")
@RestController
@RequestMapping("/member/user")
@Validated
public class MemberUserController {

    @Resource
    private UserService userService;
    @Resource
    private SysPasswordConfigServiceImpl passwordConfigService;

    @Resource
    private CouponUserService couponUserService;

    @Resource
    private MemberUserService memberUserService;
    @PostMapping("/create")
    @Operation(summary = "创建用户")
    @PreAuthorize("@ss.hasPermission('member:user:create')")
    public CommonResult<Long> createUser(@Valid @RequestBody UserCreateReqVO createReqVO) {
        return success(userService.createUser(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户")
    @PreAuthorize("@ss.hasPermission('member:user:update')")
    public CommonResult<Boolean> updateUser(@Valid @RequestBody UserUpdateReqVO updateReqVO) {
        updateReqVO.setNowMoney(null);
        updateReqVO.setIntegral(null);
        userService.updateUser(updateReqVO);
        return success(true);
    }

    @PutMapping("/updateMony")
    @Operation(summary = "更新用户余额与积分")
    @PreAuthorize("@ss.hasPermission('member:user:update')")
    public CommonResult<Boolean> updateMony(@Valid @RequestBody UserUpdateMoneyReqVO updateReqVO) {
        SysPasswordConfigVO configVO = passwordConfigService.getByType(BusiPwdEnum.BALANCE_MDF.getValue());
        if(configVO != null && com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(configVO.getPasswordValue()) && !configVO.getPasswordValue().equals(updateReqVO.getPwd())){
            throw exception(PWD_ERROR);
        }
        userService.updateMony(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('member:user:delete')")
    public CommonResult<Boolean> deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('member:user:query')")
    public CommonResult<UserRespVO> getUser(@RequestParam("id") Long id) {
        MemberUserDO user = userService.getUser(id);
        Integer couponNumb = couponUserService.getUserusableCouponNum(id);
        UserRespVO respVO = UserConvert.INSTANCE.convert(user,true);
        respVO.setUsableCouponNum(couponNumb);
        return success(respVO);
    }

    @GetMapping("/list")
    @Operation(summary = "获得用户列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('member:user:query')")
    public CommonResult<List<UserRespVO>> getUserList(@RequestParam("ids") Collection<Long> ids) {
        List<MemberUserDO> list = userService.getUserList(ids);
        return success(UserConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户分页")
    @PreAuthorize("@ss.hasPermission('member:user:query')")
    public CommonResult<PageResult<UserRespVO>> getUserPage(@Valid UserPageReqVO pageVO) {
        PageResult<MemberUserDO> pageResult = userService.getUserPage(pageVO);
        return success(UserConvert.INSTANCE.convertPage(pageResult));
    }


    @GetMapping("/points/rank")
    @Operation(summary = "积分排行分页")
    public CommonResult<List<TopPointsVO>> getPiontPage() {

        return success(memberUserService.getTop50Rank());
    }
}
