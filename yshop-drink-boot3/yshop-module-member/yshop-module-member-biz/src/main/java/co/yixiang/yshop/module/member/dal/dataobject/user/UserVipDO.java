package co.yixiang.yshop.module.member.dal.dataobject.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_vip")
public class UserVipDO {

    private Long id;
    private Long userId;
    private Integer vipLevel;
    private Long growthValue;
    private String name;
    private String phone;
    private Integer gender;
    private LocalDate birthday;
    private LocalDateTime openTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
