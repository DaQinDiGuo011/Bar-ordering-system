package co.yixiang.yshop.module.member.controller.admin.recharge;

import co.yixiang.yshop.framework.common.pojo.CommonResult;
import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.member.controller.admin.recharge.vo.RechargeOrderPageReqVO;
import co.yixiang.yshop.module.member.dal.dataobject.user.RechargeOrderDO;
import co.yixiang.yshop.module.member.service.user.RechargeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static co.yixiang.yshop.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 充值订单")
@RestController
@RequestMapping("/member/recharge-order")
@Validated
public class RechargeOrderController {

    @Resource
    private RechargeService rechargeService;

    @GetMapping("/page")
    @Operation(summary = "获得充值订单分页")
    @PreAuthorize("@ss.hasPermission('member:recharge-order:query')")
    public CommonResult<PageResult<RechargeOrderDO>> getRechargeOrderPage(@Valid RechargeOrderPageReqVO pageVO) {
        return success(rechargeService.getRechargeOrderPage(pageVO));
    }

}
