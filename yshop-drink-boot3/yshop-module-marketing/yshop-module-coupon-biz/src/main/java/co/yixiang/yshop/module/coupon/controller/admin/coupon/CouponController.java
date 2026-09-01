package co.yixiang.yshop.module.coupon.controller.admin.coupon;

import co.yixiang.yshop.framework.common.pojo.CommonResult;
import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.framework.excel.core.util.ExcelUtils;
import co.yixiang.yshop.module.coupon.controller.admin.coupon.vo.*;
import co.yixiang.yshop.module.coupon.convert.coupon.CouponConvert;
import co.yixiang.yshop.module.coupon.dal.dataobject.coupon.CouponDO;
import co.yixiang.yshop.module.coupon.service.coupon.CouponService;
import co.yixiang.yshop.module.system.controller.admin.permission.vo.permission.SysPasswordConfigVO;
import co.yixiang.yshop.module.system.service.permission.SysPasswordConfigServiceImpl;
import co.yixiang.yshop.module.system.util.oauth2.BusiPwdEnum;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static co.yixiang.yshop.framework.common.exception.util.ServiceExceptionUtil.exception;
import static co.yixiang.yshop.framework.common.pojo.CommonResult.success;
import static co.yixiang.yshop.module.system.enums.ErrorCodeConstants.PWD_ERROR;

@Tag(name = "管理后台 - 优惠券")
@RestController
@RequestMapping("/coupon/")
@Validated
public class CouponController {

    @Resource
    private CouponService Service;
    @Resource
    private SysPasswordConfigServiceImpl passwordConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建优惠券")
    @PreAuthorize("@ss.hasPermission('coupon::create')")
    public CommonResult<List<Long>> create(@Valid @RequestBody CouponCreateReqVO createReqVO) {
        return success(Service.create(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新优惠券")
    @PreAuthorize("@ss.hasPermission('coupon::update')")
    public CommonResult<Boolean> update(@Valid @RequestBody CouponUpdateReqVO updateReqVO) {
        Service.update(updateReqVO);
        return success(true);
    }

    @PostMapping("/distributeUser")
    @Operation(summary = "分配一人一券优惠券")
    @PreAuthorize("@ss.hasPermission('coupon::distributeUser')")
    public CommonResult<Boolean> distributeUser(@Valid @RequestBody CouponUpdateReqVO updateReqVO) {
        SysPasswordConfigVO configVO = passwordConfigService.getByType(BusiPwdEnum.COUPON_ALLO.getValue());
        if(configVO != null && StringUtils.isNotBlank(configVO.getPasswordValue()) && !configVO.getPasswordValue().equals(updateReqVO.getPwd())){
            throw exception(PWD_ERROR);
        }
        Service.distributeUser(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除优惠券")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('coupon::delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        Service.delete(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得优惠券")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('coupon::query')")
    public CommonResult<CouponRespVO> get(@RequestParam("id") Long id) {
        CouponDO couponDO = Service.get(id);
        return success(CouponConvert.INSTANCE.convert(couponDO));
    }

    @GetMapping("/list")
    @Operation(summary = "获得优惠券列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('coupon::query')")
    public CommonResult<List<CouponRespVO>> getList() {
        List<CouponDO> list = Service.getList();
        return success(CouponConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得优惠券分页")
    @PreAuthorize("@ss.hasPermission('coupon::query')")
    public CommonResult<PageResult<CouponRespVO>> getPage(@Valid CouponPageReqVO pageVO) {
        PageResult<CouponDO> pageResult = Service.getPage(pageVO);
        return success(CouponConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出优惠券 Excel")
    @PreAuthorize("@ss.hasPermission('coupon::export')")
    public void exportExcel(@Valid CouponExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<CouponDO> list = Service.getList(exportReqVO);
        // 导出 Excel
        List<CouponExcelVO> datas = CouponConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "优惠券.xls", "数据", CouponExcelVO.class, datas);
    }

}
