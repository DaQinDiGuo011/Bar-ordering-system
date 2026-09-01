package co.yixiang.yshop.module.order.controller.app.order;

import co.yixiang.yshop.framework.common.pojo.CommonResult;
import co.yixiang.yshop.module.order.controller.app.order.param.AppCancelParam;
import co.yixiang.yshop.module.order.controller.app.order.param.AppCreateReserveParam;
import co.yixiang.yshop.module.order.controller.app.order.param.AppTableDateParam;
import co.yixiang.yshop.module.order.controller.app.order.vo.AppBarTableVo;
import co.yixiang.yshop.module.order.controller.app.order.vo.AppReserveOrderVo;
import co.yixiang.yshop.module.order.service.storeorder.AppReserveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static co.yixiang.yshop.framework.common.pojo.CommonResult.success;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Tag(name = "用户 APP - 预定订单模块")
@RequestMapping("/reserve")
public class AppPreOrdersController {

    @Resource
    AppReserveService reserveService;

    //1.获取某日桌台状态 GET /reserve/getTableList
    @GetMapping("/getTableList")
    public CommonResult<List<AppBarTableVo>> getTableList(AppTableDateParam param){
        return success(reserveService.getTableList(param));
    }

    //2.创建预定 POST /reserve/create
    @PostMapping("/create")
    public CommonResult<Map<String, Object>> create(@RequestBody AppCreateReserveParam param){
        return success(reserveService.create(param));
    }

    //3.我的预定 GET /reserve/myOrder
    @GetMapping("/myOrder")
    public CommonResult<List<AppReserveOrderVo>> myOrder(){
        return success(reserveService.getMyOrder());
    }

    //4.取消预定 POST /reserve/cancel
    @PostMapping("/cancel")
    public CommonResult<?> cancel(@RequestBody AppCancelParam param){

        return success(reserveService.cancel(param));
    }

}
