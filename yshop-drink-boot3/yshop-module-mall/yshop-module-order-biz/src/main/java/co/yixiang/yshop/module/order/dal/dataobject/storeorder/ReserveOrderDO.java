package co.yixiang.yshop.module.order.dal.dataobject.storeorder;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("reserve_order")
@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveOrderDO {

    private Long id;
    private Long userId;
    private Long tableId;
    private String tableName;
    private String reserveDate;
    private String reserveTime;
    private String name;
    private String phone;
    private Integer peopleNum;
    private String remark;
    private Integer status; //1待使用 2已取消
    private LocalDateTime createTime;
}
