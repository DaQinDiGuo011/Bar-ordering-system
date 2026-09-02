package co.yixiang.yshop.module.member.controller.admin.recharge;

import co.yixiang.yshop.framework.common.pojo.CommonResult;
import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.member.controller.admin.recharge.vo.RechargePackagePageReqVO;
import co.yixiang.yshop.module.member.controller.admin.recharge.vo.RechargePackageSaveReqVO;
import co.yixiang.yshop.module.member.dal.dataobject.user.RechargePackageDO;
import co.yixiang.yshop.module.member.service.user.RechargeService;
import co.yixiang.yshop.module.system.controller.admin.permission.vo.permission.SysPasswordConfigVO;
import co.yixiang.yshop.module.system.service.permission.SysPasswordConfigServiceImpl;
import co.yixiang.yshop.module.system.util.oauth2.BusiPwdEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static co.yixiang.yshop.framework.common.exception.util.ServiceExceptionUtil.exception;
import static co.yixiang.yshop.framework.common.pojo.CommonResult.success;
import static co.yixiang.yshop.module.system.enums.ErrorCodeConstants.PWD_ERROR;

@Tag(name = "管理后台 - 充值套餐")
@RestController
@RequestMapping("/member/recharge-package")
@Validated
public class RechargePackageController {

    @Resource
    private RechargeService rechargeService;
    @Resource
    private SysPasswordConfigServiceImpl passwordConfigService;

    @GetMapping("/page")
    @Operation(summary = "获得充值套餐分页")
    @PreAuthorize("@ss.hasPermission('member:recharge-package:query')")
    public CommonResult<PageResult<RechargePackageDO>> getRechargePackagePage(@Valid RechargePackagePageReqVO pageVO) {
        return success(rechargeService.getRechargePackagePage(pageVO));
    }

    @PostMapping("/create")
    @Operation(summary = "创建充值套餐")
    @PreAuthorize("@ss.hasPermission('member:recharge-package:create')")
    public CommonResult<Long> createRechargePackage(@Valid @RequestBody RechargePackageSaveReqVO createReqVO) {
        checkPermissionPwd(createReqVO.getPwd());
        return success(rechargeService.createRechargePackage(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新充值套餐")
    @PreAuthorize("@ss.hasPermission('member:recharge-package:update')")
    public CommonResult<Boolean> updateRechargePackage(@Valid @RequestBody RechargePackageSaveReqVO updateReqVO) {
        checkPermissionPwd(updateReqVO.getPwd());
        rechargeService.updateRechargePackage(updateReqVO);
        return success(true);
    }

    private void checkPermissionPwd(String pwd) {
        SysPasswordConfigVO configVO = passwordConfigService.getByType(BusiPwdEnum.REGISTER_MDF.getValue());
        if (configVO != null && com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(configVO.getPasswordValue())
                && !configVO.getPasswordValue().equals(pwd)) {
            throw exception(PWD_ERROR);
        }
    }

}
