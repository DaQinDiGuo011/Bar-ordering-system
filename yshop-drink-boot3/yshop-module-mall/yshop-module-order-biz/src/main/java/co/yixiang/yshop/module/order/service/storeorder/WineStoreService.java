package co.yixiang.yshop.module.order.service.storeorder;

import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.order.controller.admin.storeorder.vo.WineStorePageReqVO;
import co.yixiang.yshop.module.order.controller.admin.storeorder.vo.WineStorePageRespVO;
import co.yixiang.yshop.module.order.controller.app.order.param.AppWineStoreParam;
import co.yixiang.yshop.module.order.controller.app.order.vo.AppWineStoreVO;

public interface WineStoreService {

    public boolean submitStore(AppWineStoreParam entity);


    public PageResult<AppWineStoreVO> getStoreRecord(AppWineStoreVO storeVO, int pageNum, int pageSize, Long userId);

    public PageResult<AppWineStoreVO> getReceiveRecord(Integer subStatus, int pageNum, int pageSize);

    public PageResult<WineStorePageRespVO> getSystemStoreRecordByPage(WineStorePageReqVO reqVO, int pageNum, int pageSize);

}
