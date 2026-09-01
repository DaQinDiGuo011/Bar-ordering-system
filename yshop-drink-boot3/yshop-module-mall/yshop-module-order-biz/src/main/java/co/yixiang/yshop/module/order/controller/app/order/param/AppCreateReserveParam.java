package co.yixiang.yshop.module.order.controller.app.order.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppCreateReserveParam {
    @NotNull(message = "桌台id不能为空")
    private Long tableId;
    @NotBlank(message = "日期不能为空")
    private String date;
    @NotBlank(message = "时间不能为空")
    private String time;
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotNull(message = "人数不能为空")
    private Integer peopleNum;
    private String remark;
}
