package co.yixiang.yshop.module.order.controller.admin.storeorder.vo;

import co.yixiang.yshop.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "每日营业额统计分页ReqVO")
public class DailyTurnoverPageReqVO extends PageParam {

    @Schema(description = "营业分割小时，默认8", example = "8")
    @Min(value = 0, message = "营业分割小时不能小于 0")
    @Max(value = 23, message = "营业分割小时不能大于 23")
    private Integer splitHour = 8;

    @Schema(description = "统计开始日期 yyyy‑MM‑dd", example = "2026‑08‑01")
    private String startDate;

    @Schema(description = "统计结束日期 yyyy‑MM‑dd", example = "2026‑08‑31")
    private String endDate;
}
