package co.yixiang.yshop.module.member.dal.dataobject.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("vip_level_config")
public class VipLevelConfigDO {

    private Long id;
    private Integer level;
    private String levelName;
    private Long needGrowth;
    private String color;
    private LocalDateTime createTime;
}
