package co.yixiang.yshop.module.order.controller.app.order;

import co.yixiang.yshop.framework.common.pojo.CommonResult;
import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.order.controller.app.order.param.AppWineStoreParam;
import co.yixiang.yshop.module.order.controller.app.order.vo.AppWineStoreVO;
import co.yixiang.yshop.module.order.service.storeorder.WineStoreServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static co.yixiang.yshop.framework.common.pojo.CommonResult.success;
import static co.yixiang.yshop.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@RestController
@RequestMapping("/user/winestore")
public class AppWineStoreController {

    @Resource
    private WineStoreServiceImpl wineStoreService;

    /**
     * 提交寄存表单
     */
    @PostMapping("/submit")
    public CommonResult<Map<String, Object>> submit(@RequestBody AppWineStoreParam dto) {
        Map<String,Object> res = new HashMap<>();


//        WineStoreDO storeDO = wineStoreService.submitStore(dto);
//        if(storeDO != null){
//            res.put("code",200);
//            res.put("msg","提交成功，请等待商家审核");
//        }else{
//            res.put("code",500);
//            res.put("msg","提交失败");
//        }
        return success(res);
    }

    /**
     * 寄存记录列表
     */
    @GetMapping("/storeRecord")
    @Parameters({
            @Parameter(name = "status", description = "寄存状态,0-所有未领取的，1待支付 2存储中 3已失效 4-领取中和已领取的 5领取中 6已领取",
                    required = true, example = "1"),
            @Parameter(name = "page", description = "页码,默认为1",
                    required = true, example = "1"),
            @Parameter(name = "pageSize", description = "页大小,默认为10",
                    required = true, example = "10")
    })
    public CommonResult<PageResult<AppWineStoreVO>> storeRecord(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ){
        AppWineStoreVO storeVO = new AppWineStoreVO();
        storeVO.setStoreStatus(status);
        PageResult<AppWineStoreVO> pageData = wineStoreService.getStoreRecord(storeVO,page,pageSize, getLoginUserId());
        return success(pageData);
    }

    /**
     * 客户领取酒水
     * @param reqVO
     * @return
     */
    @PostMapping("/receive")
    @Operation(summary = "客户领取酒水")
    public CommonResult<Map<String, String>> receiveProd(@RequestBody AppWineStoreVO reqVO){
        Map<String, String> result = new HashMap<>();

        String msg = wineStoreService.receiveProduct(reqVO);
        if(StringUtils.isBlank(msg)){
            result.put("code", "200");
        }else{
            result.put("code", "500");
            result.put("msg", msg);
        }
        return CommonResult.success(result);
    }

    @PostMapping("/cancelPay")
    @Operation(summary = "取消未支付订单")
    public CommonResult<String> cancelPay(@RequestBody AppWineStoreVO reqVO){
        wineStoreService.cancelPayOrder(reqVO.getStoreNo());

        return CommonResult.success("success");
    }

    @GetMapping("/getWineCount")
    public CommonResult<Long> getWineCount( ){
        return success(wineStoreService.getWineCount(getLoginUserId()));
    }
}
