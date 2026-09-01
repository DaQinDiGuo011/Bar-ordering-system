package co.yixiang.yshop.module.order.convert.storeorder;

import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.order.controller.admin.storeorder.vo.WineStorePageRespVO;
import co.yixiang.yshop.module.order.controller.admin.storeorder.vo.WineStoreSaveReqVO;
import co.yixiang.yshop.module.order.controller.app.order.param.AppWineStoreParam;
import co.yixiang.yshop.module.order.controller.app.order.vo.AppWineStoreVO;
import co.yixiang.yshop.module.order.dal.dataobject.storeorder.WineStoreDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface WineStoreConvert {

    WineStoreConvert INSTANCE = Mappers.getMapper(WineStoreConvert.class);

    // 创建VO → DO
    WineStoreDO convert(AppWineStoreParam bean);

    WineStoreDO convert(AppWineStoreVO bean);

    // 更新VO → DO

    // DO → 返回VO
    AppWineStoreVO convert(WineStoreDO bean);

    WineStoreDO convertSaveInfo(WineStoreSaveReqVO bean);

    WineStorePageRespVO convertInfo(WineStoreDO bean);

    // List<DO> → List<VO>
    List<AppWineStoreVO> convertList(List<WineStoreDO> list);

    WineStorePageRespVO convertPage(AppWineStoreVO storeVO);

    // 分页转换（和截图完全一致，PageResult<DO> → PageResult<VO>）
    PageResult<AppWineStoreVO> convertPage(PageResult<WineStoreDO> page);

    PageResult<WineStorePageRespVO> convertSysPage(PageResult<AppWineStoreVO> page);

}
