package co.yixiang.yshop.module.order.controller.admin.storeorder;

import cn.hutool.core.util.IdUtil;
import co.yixiang.yshop.framework.common.pojo.CommonResult;
import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.order.controller.admin.storeorder.vo.WineStorePageReqVO;
import co.yixiang.yshop.module.order.controller.admin.storeorder.vo.WineStorePageRespVO;
import co.yixiang.yshop.module.order.controller.admin.storeorder.vo.WineStoreReqVO;
import co.yixiang.yshop.module.order.controller.admin.storeorder.vo.WineStoreSaveReqVO;
import co.yixiang.yshop.module.order.convert.storeorder.WineStoreConvert;
import co.yixiang.yshop.module.order.dal.dataobject.storeorder.WineStoreDO;
import co.yixiang.yshop.module.order.service.storeorder.WineStoreServiceImpl;
import co.yixiang.yshop.module.product.dal.dataobject.storeproduct.StoreProductDO;
import co.yixiang.yshop.module.product.service.storeproduct.StoreProductService;
import co.yixiang.yshop.module.system.controller.admin.permission.vo.permission.SysPasswordConfigVO;
import co.yixiang.yshop.module.system.service.permission.SysPasswordConfigServiceImpl;
import co.yixiang.yshop.module.system.util.oauth2.BusiPwdEnum;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static co.yixiang.yshop.framework.common.exception.util.ServiceExceptionUtil.exception;
import static co.yixiang.yshop.module.system.enums.ErrorCodeConstants.PWD_ERROR;

@Slf4j
@RestController
@RequestMapping("/system/winestore")
public class WineStoreController {

    @Resource
    private WineStoreServiceImpl wineStoreService;

    @Resource
    private StoreProductService storeProductService;
    @Resource
    private SysPasswordConfigServiceImpl passwordConfigService;

    @GetMapping("/page")
    @Operation(summary = "分页查询寄存记录")
    public CommonResult<PageResult<WineStorePageRespVO>> getPage(@Valid WineStorePageReqVO query) {

        PageResult<WineStorePageRespVO> page = wineStoreService.getSystemStoreRecordByPage(query, query.getPageNo(), query.getPageSize());


//        // 状态文本赋值
        page.getList().forEach(item->{
            String desc = switch (item.getStoreStatus()){
                case 1 -> "待支付";
                case 2 -> "存储中";
                case 3 -> "已失效";
                case 4 -> "领取中";
                case 5 -> "已领取";
                default -> "未知";
            };
            item.setStoreStatusDesc(desc);
//            StoreProductDO productDO = storeProductService.getStoreProduct(item.getProductId());
//            if(productDO != null){
//                item.setProductName(productDO.getStoreName());
//            }
        });
        return CommonResult.success(page);
    }

    @PostMapping("/update-status")
    @Operation(summary = "修改寄存状态（领取/失效）")
    public CommonResult<Boolean> updateStatus(@RequestBody WineStoreReqVO reqVO){
        WineStoreDO storeDO = wineStoreService.getById(reqVO.getId());
        if(storeDO == null){
            return CommonResult.success(false);
        }
        storeDO.setStoreStatus(reqVO.getStoreStatus());
        if(reqVO.getStoreStatus() == 4 || reqVO.getStoreStatus() == 5){
            storeDO.setReceiveTime(LocalDateTime.now());
        }
        return CommonResult.success(wineStoreService.saveOrUpdate(storeDO));
    }

    @PostMapping("/save-info")
    @Operation(summary = "新增/修改寄存信息")
    public CommonResult<Boolean> saveInfo(@RequestBody WineStoreSaveReqVO storeSaveReqVO){
        SysPasswordConfigVO configVO = passwordConfigService.getByType(BusiPwdEnum.REGISTER_MDF.getValue());
        if(configVO != null && com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(configVO.getPasswordValue()) && !configVO.getPasswordValue().equals(storeSaveReqVO.getPwd())){
            throw exception(PWD_ERROR);
        }

        WineStoreDO storeDO = WineStoreConvert.INSTANCE.convertSaveInfo(storeSaveReqVO);
        log.info("新增storeNo={},判断结果：{}",storeDO.getStoreNo(),StringUtils.isBlank(storeDO.getStoreNo()));
        if(StringUtils.isBlank(storeDO.getStoreNo())){
            String orderSn = IdUtil.getSnowflake(0, 0).nextIdStr();
            storeDO.setStoreNo("JC" + orderSn);
            storeDO.setCreateTime(LocalDateTime.now());
        }
        if(storeDO.getStoreStatus() == 4 || storeDO.getStoreStatus() == 5){
            storeDO.setReceiveTime(LocalDateTime.now());
        }
        storeDO.setUpdateTime(LocalDateTime.now());
        return CommonResult.success(wineStoreService.saveOrUpdate(storeDO));
    }

    @GetMapping("/getInfo/{id}")
    @Operation(summary = "通过id查询信息")
    public CommonResult<WineStorePageRespVO> saveInfo(@PathVariable Long id){
        WineStoreDO storeDO = wineStoreService.getById(id);
        WineStorePageRespVO respVO = WineStoreConvert.INSTANCE.convertInfo(storeDO);

        StoreProductDO productDO = storeProductService.getStoreProduct(storeDO.getProductId());
        if(productDO != null){
            respVO.setStoreName(productDO.getStoreName());
        }
        return CommonResult.success(respVO);
    }
}
