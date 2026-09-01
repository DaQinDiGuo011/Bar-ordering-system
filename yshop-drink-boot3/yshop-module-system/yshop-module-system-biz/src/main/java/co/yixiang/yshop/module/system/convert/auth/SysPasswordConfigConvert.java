package co.yixiang.yshop.module.system.convert.auth;

import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.system.controller.admin.permission.vo.permission.SysPasswordConfigVO;
import co.yixiang.yshop.module.system.dal.dataobject.permission.SysPasswordConfigDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SysPasswordConfigConvert {

    SysPasswordConfigConvert INSTANCE = Mappers.getMapper(SysPasswordConfigConvert.class);
    SysPasswordConfigVO convertResp(SysPasswordConfigDO configDO);

    List<SysPasswordConfigVO> converList(List<SysPasswordConfigDO> list);

    SysPasswordConfigDO convertReq(SysPasswordConfigVO configDO);
    PageResult<SysPasswordConfigVO> convertPage(PageResult<SysPasswordConfigDO> page);
}
