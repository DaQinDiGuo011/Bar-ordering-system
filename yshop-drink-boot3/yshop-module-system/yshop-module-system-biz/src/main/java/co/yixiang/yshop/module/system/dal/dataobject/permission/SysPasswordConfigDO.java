package co.yixiang.yshop.module.system.dal.dataobject.permission;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_password_config")
public class SysPasswordConfigDO{

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 密码类型编码 */
    private String passwordType;

    /** 密码类型展示名称 */
    private String passwordName;

    /** 密码/密钥值 */
    private String passwordValue;

    /** 是否生效 0禁用 1生效 */
    private Integer enabled;

    /** 备注 */
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @TableLogic
    private Boolean deleted;
}
