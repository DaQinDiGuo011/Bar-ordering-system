package co.yixiang.yshop.module.order.service.storeorder;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.yshop.framework.common.enums.OrderInfoEnum;
import co.yixiang.yshop.framework.common.pojo.CommonResult;
import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.member.controller.admin.user.vo.UserRespVO;
import co.yixiang.yshop.module.member.convert.user.UserConvert;
import co.yixiang.yshop.module.member.dal.dataobject.user.MemberUserDO;
import co.yixiang.yshop.module.member.dal.dataobject.useraddress.UserAddressDO;
import co.yixiang.yshop.module.member.enums.BillDetailEnum;
import co.yixiang.yshop.module.member.service.userbill.UserBillService;
import co.yixiang.yshop.module.message.supply.WeiXinSubscribeService;
import co.yixiang.yshop.module.order.controller.admin.storeorder.vo.WineStorePageReqVO;
import co.yixiang.yshop.module.order.controller.admin.storeorder.vo.WineStorePageRespVO;
import co.yixiang.yshop.module.order.controller.app.order.param.AppWineStoreParam;
import co.yixiang.yshop.module.order.controller.app.order.vo.AppStoreOrderQueryVo;
import co.yixiang.yshop.module.order.controller.app.order.vo.AppWineStoreVO;
import co.yixiang.yshop.module.order.convert.storeorder.WineStoreConvert;
import co.yixiang.yshop.module.order.dal.dataobject.ordernumber.OrderNumberDO;
import co.yixiang.yshop.module.order.dal.dataobject.storeorder.StoreOrderDO;
import co.yixiang.yshop.module.order.dal.dataobject.storeorder.WineStoreDO;
import co.yixiang.yshop.module.order.dal.mysql.ordernumber.OrderNumberMapper;
import co.yixiang.yshop.module.order.dal.mysql.storeorder.StoreOrderMapper;
import co.yixiang.yshop.module.order.dal.mysql.storeorder.WineStoreMapper;
import co.yixiang.yshop.module.order.enums.OrderLogEnum;
import co.yixiang.yshop.module.order.service.storeordercartinfo.StoreOrderCartInfoService;
import co.yixiang.yshop.module.product.dal.dataobject.storeproduct.StoreProductDO;
import co.yixiang.yshop.module.product.service.storeproduct.StoreProductService;
import co.yixiang.yshop.module.store.service.storeshop.AppStoreShopService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static co.yixiang.yshop.framework.common.exception.util.ServiceExceptionUtil.exception;
import static co.yixiang.yshop.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static co.yixiang.yshop.module.member.enums.ErrorCodeConstants.USER_ADDRESS_NOT_EXISTS;
import static co.yixiang.yshop.module.order.enums.ErrorCodeConstants.ORDER_GEN_FAIL;
import static co.yixiang.yshop.module.order.enums.ErrorCodeConstants.SELECT_ADDRESS;

@Slf4j
@Service
@Validated
public class WineStoreServiceImpl extends ServiceImpl<WineStoreMapper, WineStoreDO>{

    @Resource
    private WineStoreMapper wineStoreMapper;

    @Resource
    private OrderNumberMapper orderNumberMapper;
    @Resource
    private StoreProductService storeProductService;
    @Resource
    private StoreOrderCartInfoService storeOrderCartInfoService;
    @Resource
    private UserBillService billService;
    @Resource
    private WeiXinSubscribeService weiXinSubscribeService;
    @Resource
    private StoreOrderMapper storeOrderMapper;

    @Resource
    @Lazy
    private AppStoreOrderServiceImpl storeOrderService;

    public WineStoreDO submitStore(AppWineStoreParam entity) {
        WineStoreDO storeDO = WineStoreConvert.INSTANCE.convert(entity);
        storeDO.setStoreStatus(1); // 审核中
        storeDO.setDeleted(0);
        storeDO.setCreateTime(LocalDateTime.now());
        storeDO.setUpdateTime(LocalDateTime.now());
        storeDO.setCreator(String.valueOf(getLoginUserId()));
        storeDO.setUpdater(String.valueOf(getLoginUserId()));
        save(storeDO);
        return storeDO;
    }

    public List<WineStoreDO> getStoreByStoreNo(String storeNo){
        LambdaQueryWrapper<WineStoreDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WineStoreDO::getStoreNo, storeNo);
        wrapper.eq(WineStoreDO::getDeleted, 0);
        List<WineStoreDO> storeVOList = wineStoreMapper.selectList(wrapper);
        return storeVOList;
    }
    public PageResult<AppWineStoreVO> getStoreRecord(AppWineStoreVO storeVO, int pageNum, int pageSize, Long userId) {
        // 构建查询条件
        LambdaQueryWrapper<WineStoreDO> wrapper = new LambdaQueryWrapper<>();

        if(userId != null){
            wrapper.eq(WineStoreDO::getUserId, userId);
        }
        if(storeVO.getRealName() != null){
            wrapper.eq(WineStoreDO::getRealName, storeVO.getRealName());
        }
        if(storeVO.getPhone() != null){
            wrapper.eq(WineStoreDO::getPhone, storeVO.getPhone());
        }
        if(storeVO.getStoreNo() != null){
            wrapper.eq(WineStoreDO::getStoreNo, storeVO.getStoreNo());
        }
        if(storeVO.getUserId() != null){
            wrapper.eq(WineStoreDO::getUserId, storeVO.getUserId());
        }
        wrapper.eq(WineStoreDO::getDeleted, 0);
        if(storeVO.getStoreStatus() != null){
            //等于0则查所有，1、2、3查对应的状态1、2、3，4查4和5，5查4，6查5
            if(0 == storeVO.getStoreStatus()){
                wrapper.in(WineStoreDO::getStoreStatus, 1,2,3);
                wrapper.orderByDesc(WineStoreDO::getCreateTime);
            }else if(4 == storeVO.getStoreStatus()){
                wrapper.in(WineStoreDO::getStoreStatus, 4,5);
                wrapper.orderByDesc(WineStoreDO::getReceiveTime);
            }else if(5 == storeVO.getStoreStatus()){
                wrapper.eq(WineStoreDO::getStoreStatus, 4);
                wrapper.orderByDesc(WineStoreDO::getReceiveTime);
            }else if(6 == storeVO.getStoreStatus()){
                wrapper.eq(WineStoreDO::getStoreStatus, 5);
                wrapper.orderByDesc(WineStoreDO::getReceiveTime);
            }else {
                wrapper.orderByDesc(WineStoreDO::getCreateTime);
                wrapper.eq(WineStoreDO::getStoreStatus, storeVO.getStoreStatus());
            }

        }

        // 分页偏移量计算
        int offset = (pageNum - 1) * pageSize;
        // 查询列表数据
        List<WineStoreDO> recordList = wineStoreMapper.selectPageList(wrapper, offset, pageSize);
        // 查询总数量
        Long total = wineStoreMapper.selectCount(wrapper);

        // 组装统一分页结果 PageResult
        PageResult<WineStoreDO> pageResult = new PageResult<>(recordList, total);

        // DO → VO 分页转换（MapStruct）
        PageResult<AppWineStoreVO> wineStoreRespVO = WineStoreConvert.INSTANCE.convertPage(pageResult);

        // 循环填充明细、用户信息、状态文本（和截图逻辑一模一样）
        for (AppWineStoreVO respVO : wineStoreRespVO.getList()) {
            StoreProductDO productDO = storeProductService.getStoreProduct(respVO.getProductId());
            if(productDO != null){
                respVO.setStoreName(productDO.getStoreName());
                respVO.setImage(productDO.getImage());
            }else{
                respVO.setStoreName("--未找到商品信息");
            }

        }
        return wineStoreRespVO;
    }

    public PageResult<WineStorePageRespVO> getSystemStoreRecordByPage(WineStorePageReqVO reqVO, int pageNum, int pageSize){
        AppWineStoreVO param = new AppWineStoreVO();
        if(StringUtils.isNotBlank(reqVO.getPhone())){
            param.setPhone(reqVO.getPhone());
        }
        if(reqVO.getStoreStatus() != null){
            param.setStoreStatus(reqVO.getStoreStatus());
        }
        if(StringUtils.isNotBlank(reqVO.getRealName())){
            param.setRealName(reqVO.getRealName());
        }
        if(StringUtils.isNotBlank(reqVO.getStoreNo())){
            param.setStoreNo(reqVO.getStoreNo());
        }
        if(reqVO.getUserId() != null){
            param.setUserId(reqVO.getUserId());
        }

        PageResult<AppWineStoreVO> storeVOPageResult = getStoreRecord(param, pageNum, pageSize, null);

        PageResult<WineStorePageRespVO> storePageRespVOPageResult = new PageResult<>();
        storePageRespVOPageResult.setTotal(storeVOPageResult.getTotal());
        List<WineStorePageRespVO> storePageRespVOList = new ArrayList<>();
        storeVOPageResult.getList().stream().forEach(info -> {
            WineStorePageRespVO respVO = WineStoreConvert.INSTANCE.convertPage(info);
            storePageRespVOList.add(respVO);
        });

        storePageRespVOPageResult.setList(storePageRespVOList);
        return storePageRespVOPageResult;
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String receiveProduct(AppWineStoreVO reqVO){
        WineStoreDO wineStoreDO = this.getById(reqVO.getId());

        if(wineStoreDO == null){
            return "寄存订单不存在";
        }
        if(reqVO.getNum() == null || reqVO.getNum() == 0){
            return "领取数量不能为0";
        }
        if(reqVO.getNum() > wineStoreDO.getNum()){
            return "领取数量不能超过寄存数量";
        }
        StoreProductDO productDO = storeProductService.getStoreProduct(reqVO.getProductId());
        if(productDO == null){
            return "商品不存在";
        }
        String wineId = null;
        BigDecimal payPrice = BigDecimal.ZERO;
        //领取完了则直接改状态，未领取完则减少数量，并生成新的领取数据
        if(reqVO.getNum() == wineStoreDO.getNum()){
            wineStoreDO.setStoreStatus(4);
            wineStoreDO.setReceiveTime(LocalDateTime.now());
            payPrice = wineStoreDO.getActualPayPrice();
            wineId = wineStoreDO.getStoreNo();
        }else{
            WineStoreDO receiveStore = new WineStoreDO();
            receiveStore.setNum(reqVO.getNum());
            receiveStore.setProductId(reqVO.getProductId());
            receiveStore.setPhone(wineStoreDO.getPhone());
            receiveStore.setStoreNo(wineStoreDO.getStoreNo());
            receiveStore.setUserId(wineStoreDO.getUserId());
            receiveStore.setRealName(wineStoreDO.getRealName());
            receiveStore.setStoreStatus(4); // 领取中
            receiveStore.setDeleted(0);
            receiveStore.setCreateTime(LocalDateTime.now());
            receiveStore.setUpdateTime(LocalDateTime.now());
            receiveStore.setReceiveTime(LocalDateTime.now());
            receiveStore.setCreator(String.valueOf(getLoginUserId()));
            receiveStore.setUpdater(String.valueOf(getLoginUserId()));

            payPrice = wineStoreDO.getActualPayPrice().divide(new BigDecimal(String.valueOf(wineStoreDO.getNum())), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(String.valueOf(reqVO.getNum())));
            receiveStore.setActualPayPrice(payPrice);
            receiveStore.setCouponPrice(wineStoreDO.getCouponPrice());
            receiveStore.setTotalPrice(wineStoreDO.getTotalPrice());
            save(receiveStore);

            wineId = receiveStore.getStoreNo();
            wineStoreDO.setNum(wineStoreDO.getNum() - reqVO.getNum());
        }
        wineStoreDO.setUpdateTime(LocalDateTime.now());
        this.updateById(wineStoreDO);


        //创建订单
        StoreOrderDO storeOrder = new StoreOrderDO();
        //生成分布式唯一值
        String orderSn = IdUtil.getSnowflake(0, 0).nextIdStr();
        //添加取餐表
        OrderNumberDO orderNumberDO = OrderNumberDO.builder().orderId(orderSn).build();
        orderNumberMapper.insert(orderNumberDO);

        //组合数据
        LocalDateTime localDateTime = LocalDateTime.now();
        storeOrder.setNumberId(orderNumberDO.getId());
        storeOrder.setUid(getLoginUserId());
        storeOrder.setOrderId(orderSn);
        //处理如果是外卖 地址

        storeOrder.setCartId("");
        storeOrder.setTotalNum(reqVO.getNum());
        storeOrder.setTotalPrice(NumberUtil.mul(String.valueOf(reqVO.getNum()),
                productDO.getPrice().toString()));


        storeOrder.setPayPrice(payPrice);
        storeOrder.setStatus(0);
        storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
        storeOrder.setPayType(wineStoreDO.getPayType());
        storeOrder.setShopId(reqVO.getShopId());
        storeOrder.setShopName(reqVO.getShopName());
        storeOrder.setUseIntegral(0);
        storeOrder.setBackIntegral(0);
        storeOrder.setCost(BigDecimal.ZERO);
        //storeOrder.setUnique(key);
        storeOrder.setShippingType(OrderInfoEnum.SHIPPIING_TYPE_1.getValue());
        storeOrder.setOrderType("takein");
        storeOrder.setDeskNumber(reqVO.getDeskNumber());
        storeOrder.setMark("寄存领取：" + wineId);
        storeOrder.setTotalPrice(wineStoreDO.getTotalPrice());
        storeOrder.setCouponPrice(wineStoreDO.getCouponPrice());
        storeOrder.setPayPrice(payPrice);

        storeOrderMapper.insert(storeOrder);

        String specs = reqVO.getSpec()== null? "":reqVO.getSpec();
        storeOrderCartInfoService.saveCartInfo(storeOrder.getId(), storeOrder.getOrderId(),
                List.of(String.valueOf(productDO.getId())),List.of(String.valueOf(reqVO.getNum())), List.of(specs));

        weiXinSubscribeService.sendMsgToAdmin(storeOrder.getOrderId());

        billService.expend(getLoginUserId(), "寄存领取",
                BillDetailEnum.CATEGORY_3.getValue(),
                BillDetailEnum.TYPE_3.getValue(),
                0, 0,
                "无需支付，直接领取商品");
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void cancelPayOrder(String orderId){

        List<WineStoreDO> storeDOList = getStoreByStoreNo(orderId).stream().filter(info -> info.getStoreStatus() == 1).collect(Collectors.toList());
        if(storeDOList.isEmpty()){
            return;
        }

        for(WineStoreDO storeDO: storeDOList){
            storeDO.setDeleted(1);
            storeDO.setUpdateTime(LocalDateTime.now());
            wineStoreMapper.updateById(storeDO);
        }

        AppStoreOrderQueryVo order = new AppStoreOrderQueryVo();
        order.setStatus(-1);
        order.setPaid(0);
        order.setCouponIdList(storeDOList.get(0).getCouponIdList());
        order.setUid(getLoginUserId());

        storeOrderService.regressionCoupon(order, 0);
    }

    public Long getWineCount(Long userId){
        LambdaQueryWrapper<WineStoreDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WineStoreDO::getUserId, userId);
        wrapper.eq(WineStoreDO::getStoreStatus, 2);
        wrapper.eq(WineStoreDO::getDeleted, 0);
        Long count = wineStoreMapper.selectCount(wrapper);
        return count;
    }

}
