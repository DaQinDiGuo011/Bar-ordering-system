package co.yixiang.yshop.module.store.controller.admin.storeshop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 门店管理 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StoreShopRespVO extends StoreShopBaseVO {

    @Schema(description = "id", required = true, example = "25450")
    private Long id;

    @Schema(description = "添加时间")
    private LocalDateTime createTime;

    private List<String> adImageList;

}
