package co.yixiang.yshop.module.member.service.user;

import co.yixiang.yshop.module.member.controller.app.user.vo.StorePointExchangeVO;
import co.yixiang.yshop.module.member.controller.app.user.vo.StorePointGoodsVO;
import co.yixiang.yshop.module.member.controller.app.user.vo.UserPointLogVO;
import co.yixiang.yshop.module.member.dal.dataobject.user.MemberUserDO;
import co.yixiang.yshop.module.member.dal.dataobject.user.StorePointExchangeDO;
import co.yixiang.yshop.module.member.dal.dataobject.user.StorePointGoodsDO;
import co.yixiang.yshop.module.member.dal.dataobject.user.UserPointLogDO;
import co.yixiang.yshop.module.member.dal.mysql.user.MemberUserMapper;
import co.yixiang.yshop.module.member.dal.mysql.user.PointExchangeMapper;
import co.yixiang.yshop.module.member.dal.mysql.user.PointGoodsMapper;
import co.yixiang.yshop.module.member.dal.mysql.user.UserPointLogMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

import static co.yixiang.yshop.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Service
@Validated
public class PointServiceImpl implements PointService{

    @Resource
    private MemberUserMapper memberUserMapper;

    @Resource
    private UserPointLogMapper logMapper;
    @Resource
    private PointGoodsMapper goodsMapper;
    @Resource
    private PointExchangeMapper exchangeMapper;

    @Override
    public List<UserPointLogVO> getPointLog() {
        return logMapper.selectLog(getLoginUserId()).stream().map(info -> {
            UserPointLogVO logVO = new UserPointLogVO();
            logVO.setId(info.getId());
            logVO.setType(info.getType());
            logVO.setRemark(info.getRemark());
            logVO.setPoint(info.getPoint());
            return logVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<StorePointGoodsVO> getShopGoods() {
        return goodsMapper.selectOnlineGoods().stream().map(info -> {
            StorePointGoodsVO goodsVO = new StorePointGoodsVO();
            goodsVO.setId(info.getId());
            goodsVO.setName(info.getName());
            goodsVO.setImage(info.getImage());
            goodsVO.setStock(info.getStock());
            goodsVO.setStatus(info.getStatus());
            goodsVO.setNeedPoint(info.getNeedPoint());
            goodsVO.setCreatedAt(info.getCreatedAt());
            return goodsVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<StorePointExchangeVO> getExchangeList(Integer status) {
        return exchangeMapper.selectUserExchange(getLoginUserId(),status).stream().map(info -> {
            StorePointExchangeVO exchangeVO = new StorePointExchangeVO();
            exchangeVO.setId(info.getId());
            exchangeVO.setStatus(info.getStatus());
            exchangeVO.setUsePoint(info.getUsePoint());
            exchangeVO.setGoodsId(info.getGoodsId());
            exchangeVO.setGoodsName(info.getGoodsName());
            exchangeVO.setOrderNo(info.getOrderNo());
            exchangeVO.setCreatedAt(info.getCreatedAt());
            return exchangeVO;
        }).collect(Collectors.toList());
    }

    @Override
    public String exchangeGoods(Integer goodsId) {
        StorePointGoodsDO goods = goodsMapper.selectById(goodsId);
        if(goods == null) return "商品不存在";
        if(goods.getStock() <=0) return "商品库存不足";

        Long userId = getLoginUserId();
        MemberUserDO userPoint = memberUserMapper.selectById(userId);
        if(userPoint.getIntegral() < goods.getNeedPoint()){
            return "积分不足";
        }

        // 扣积分
        memberUserMapper.subPoint(userId, goods.getNeedPoint());
        // 写入消耗流水 type=2
        UserPointLogDO log = new UserPointLogDO();
        log.setUserId(userId);
        log.setType(2);
        log.setPoint(goods.getNeedPoint());
        log.setRemark("积分兑换："+goods.getName());
        logMapper.insert(log);

        // 创建兑换订单
        StorePointExchangeDO exchange = new StorePointExchangeDO();
        exchange.setOrderNo("PT"+System.currentTimeMillis());
        exchange.setUserId(userId);
        exchange.setGoodsId(goodsId);
        exchange.setGoodsName(goods.getName());
        exchange.setUsePoint(goods.getNeedPoint());
        exchange.setStatus(0); //未完成
        exchangeMapper.insert(exchange);

        return null;
    }
}
