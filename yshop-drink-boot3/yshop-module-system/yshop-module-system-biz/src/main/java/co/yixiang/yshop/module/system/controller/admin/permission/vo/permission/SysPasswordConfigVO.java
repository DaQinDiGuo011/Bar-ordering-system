package co.yixiang.yshop.module.system.controller.admin.permission.vo.permission;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysPasswordConfigVO {

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

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String creator;

    private String updater;

    private Boolean deleted;
}
