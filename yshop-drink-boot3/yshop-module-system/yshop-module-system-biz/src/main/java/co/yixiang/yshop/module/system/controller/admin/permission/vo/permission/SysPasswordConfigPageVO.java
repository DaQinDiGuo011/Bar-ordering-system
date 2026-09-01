package co.yixiang.yshop.module.system.controller.admin.permission.vo.permission;

import co.yixiang.yshop.framework.common.pojo.PageParam;
import lombok.Data;

@Data
public class SysPasswordConfigPageVO extends PageParam {
    private String passwordType;
}
