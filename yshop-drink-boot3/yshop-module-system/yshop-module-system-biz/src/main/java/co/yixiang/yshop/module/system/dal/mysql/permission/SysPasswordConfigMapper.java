package co.yixiang.yshop.module.system.dal.mysql.permission;

import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.framework.mybatis.core.mapper.BaseMapperX;
import co.yixiang.yshop.framework.mybatis.core.query.LambdaQueryWrapperX;
import co.yixiang.yshop.module.system.controller.admin.permission.vo.permission.SysPasswordConfigPageVO;
import co.yixiang.yshop.module.system.dal.dataobject.permission.SysPasswordConfigDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysPasswordConfigMapper extends BaseMapperX<SysPasswordConfigDO> {

    default PageResult<SysPasswordConfigDO> selectPage(SysPasswordConfigPageVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SysPasswordConfigDO>()
                .eqIfPresent(SysPasswordConfigDO::getPasswordType, reqVO.getPasswordType()));
    }

}
