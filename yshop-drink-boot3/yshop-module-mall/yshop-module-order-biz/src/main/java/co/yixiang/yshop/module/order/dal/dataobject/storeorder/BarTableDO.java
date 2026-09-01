package co.yixiang.yshop.module.order.dal.dataobject.storeorder;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
@TableName("bar_table")
@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarTableDO {
    private Long id;
    private String tableName;
    private LocalDateTime createTime;
}
