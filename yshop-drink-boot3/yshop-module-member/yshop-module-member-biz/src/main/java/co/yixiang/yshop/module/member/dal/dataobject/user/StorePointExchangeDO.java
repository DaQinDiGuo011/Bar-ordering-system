package co.yixiang.yshop.module.member.dal.dataobject.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorePointExchangeDO {

    private Integer id;
    private String orderNo;
    private Long userId;
    private Integer goodsId;
    private String goodsName;
    private Integer usePoint;
    /** 0未完成 1已完成 */
    private Integer status;
    private LocalDateTime createdAt;

}
