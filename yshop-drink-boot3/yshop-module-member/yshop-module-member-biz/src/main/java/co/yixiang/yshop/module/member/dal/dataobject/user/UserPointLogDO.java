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
public class UserPointLogDO {

    private Integer id;
    private Long userId;
    /** 1收入 2消耗 */
    private Integer type;
    private Integer point;
    private String remark;
    private LocalDateTime createdAt;

}
