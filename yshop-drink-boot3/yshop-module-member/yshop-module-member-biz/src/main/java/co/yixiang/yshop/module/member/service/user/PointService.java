package co.yixiang.yshop.module.member.service.user;

import co.yixiang.yshop.module.member.controller.app.user.vo.StorePointExchangeVO;
import co.yixiang.yshop.module.member.controller.app.user.vo.StorePointGoodsVO;
import co.yixiang.yshop.module.member.controller.app.user.vo.UserPointLogVO;

import java.util.List;

public interface PointService {

    // 获取积分明细
    public List<UserPointLogVO> getPointLog();

    // 获取积分商城商品
    public List<StorePointGoodsVO> getShopGoods();

    // 获取兑换记录
    public List<StorePointExchangeVO> getExchangeList(Integer status);

    // 积分兑换商品【事务】
    public String exchangeGoods(Integer goodsId);
}
