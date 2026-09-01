package co.yixiang.yshop.module.order.controller.app.order.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppCancelParam {

    @NotNull(message = "订单id不能为空")
    private Long id;
}
