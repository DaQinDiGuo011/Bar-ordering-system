package co.yixiang.yshop.module.system.service.permission;

import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.system.controller.admin.permission.vo.permission.SysPasswordConfigPageVO;
import co.yixiang.yshop.module.system.controller.admin.permission.vo.permission.SysPasswordConfigVO;

import java.util.List;

public interface SysPasswordConfigService {

    SysPasswordConfigVO getByType(String passwordType);

    List<SysPasswordConfigVO> getValidInfoList();

    void addInfo(SysPasswordConfigVO configDO);

    void updInfo(SysPasswordConfigVO configDO);

    PageResult<SysPasswordConfigVO> selectPage(SysPasswordConfigPageVO reqVO);
}
