package co.yixiang.yshop.module.order.service.storeorder;

import co.yixiang.yshop.module.order.controller.app.order.param.AppCancelParam;
import co.yixiang.yshop.module.order.controller.app.order.param.AppCreateReserveParam;
import co.yixiang.yshop.module.order.controller.app.order.param.AppTableDateParam;
import co.yixiang.yshop.module.order.controller.app.order.vo.AppBarTableVo;
import co.yixiang.yshop.module.order.controller.app.order.vo.AppReserveOrderVo;

import java.util.List;
import java.util.Map;

public interface AppReserveService {

    List<AppBarTableVo> getTableList(AppTableDateParam param);

    Map<String, Object> create(AppCreateReserveParam param);

    List<AppReserveOrderVo> getMyOrder();

    Map<String, Object> cancel(AppCancelParam param);
}
