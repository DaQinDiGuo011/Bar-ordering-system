package co.yixiang.yshop.module.order.service.storeorder;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import co.yixiang.yshop.framework.common.constant.ShopConstants;
import co.yixiang.yshop.framework.common.enums.OrderInfoEnum;
import co.yixiang.yshop.framework.common.enums.PayIdEnum;
import co.yixiang.yshop.framework.common.enums.ShopCommonEnum;
import co.yixiang.yshop.framework.common.exception.ErrorCode;
import co.yixiang.yshop.framework.tenant.core.aop.TenantIgnore;
import co.yixiang.yshop.module.coupon.dal.dataobject.couponuser.CouponUserDO;
import co.yixiang.yshop.module.coupon.service.couponuser.AppCouponUserService;
import co.yixiang.yshop.module.member.controller.app.user.vo.AppUserQueryVo;
import co.yixiang.yshop.module.member.dal.dataobject.user.MemberUserDO;
import co.yixiang.yshop.module.member.dal.dataobject.useraddress.UserAddressDO;
import co.yixiang.yshop.module.member.dal.dataobject.userbill.UserBillDO;
import co.yixiang.yshop.module.member.enums.BillDetailEnum;
import co.yixiang.yshop.module.member.service.user.MemberUserService;
import co.yixiang.yshop.module.member.service.useraddress.AppUserAddressService;
import co.yixiang.yshop.module.member.service.userbill.UserBillService;
import co.yixiang.yshop.module.message.mq.producer.WeixinNoticeProducer;
import co.yixiang.yshop.module.message.redismq.msg.OrderMsg;
import co.yixiang.yshop.module.message.supply.WeiXinSubscribeService;
import co.yixiang.yshop.module.order.controller.app.order.param.AppOrderParam;
import co.yixiang.yshop.module.order.controller.app.order.param.AppPayParam;
import co.yixiang.yshop.module.order.controller.app.order.param.AppWineStoreParam;
import co.yixiang.yshop.module.order.controller.app.order.vo.AppStoreOrderQueryVo;
import co.yixiang.yshop.module.order.convert.storeorder.StoreOrderConvert;
import co.yixiang.yshop.module.order.dal.dataobject.ordernumber.OrderNumberDO;
import co.yixiang.yshop.module.order.dal.dataobject.storeorder.StoreOrderDO;
import co.yixiang.yshop.module.order.dal.dataobject.storeorder.WineStoreDO;
import co.yixiang.yshop.module.order.dal.dataobject.storeorder.YshopPayWechatDO;
import co.yixiang.yshop.module.order.dal.dataobject.storeordercartinfo.StoreOrderCartInfoDO;
import co.yixiang.yshop.module.order.dal.mysql.ordernumber.OrderNumberMapper;
import co.yixiang.yshop.module.order.dal.mysql.storeorder.StoreOrderMapper;
import co.yixiang.yshop.module.order.dal.mysql.storeorder.WineStoreMapper;
import co.yixiang.yshop.module.order.enums.AppFromEnum;
import co.yixiang.yshop.module.order.enums.OrderLogEnum;
import co.yixiang.yshop.module.order.enums.OrderStatusEnum;
import co.yixiang.yshop.module.order.enums.PayTypeEnum;
import co.yixiang.yshop.module.order.service.storeorder.dto.StatusDto;
import co.yixiang.yshop.module.order.service.storeordercartinfo.StoreOrderCartInfoService;
import co.yixiang.yshop.module.order.service.storeorderstatus.StoreOrderStatusService;
import co.yixiang.yshop.module.pay.dal.dataobject.merchantdetails.MerchantDetailsDO;
import co.yixiang.yshop.module.pay.service.merchantdetails.MerchantDetailsService;
import co.yixiang.yshop.module.product.dal.dataobject.storeproduct.StoreProductDO;
import co.yixiang.yshop.module.product.dal.dataobject.storeproductattrvalue.StoreProductAttrValueDO;
import co.yixiang.yshop.module.product.service.storeproduct.AppStoreProductService;
import co.yixiang.yshop.module.product.service.storeproduct.StoreProductService;
import co.yixiang.yshop.module.product.service.storeproductattrvalue.StoreProductAttrValueService;
import co.yixiang.yshop.module.store.convert.storeshop.StoreShopConvert;
import co.yixiang.yshop.module.store.dal.dataobject.storeshop.StoreShopDO;
import co.yixiang.yshop.module.store.service.storeshop.AppStoreShopService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.egzosn.pay.spring.boot.core.bean.MerchantPayOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static co.yixiang.yshop.framework.common.exception.util.ServiceExceptionUtil.exception;
import static co.yixiang.yshop.module.member.enums.ErrorCodeConstants.*;
import static co.yixiang.yshop.module.order.enums.ErrorCodeConstants.*;

/**
 * 订单 Service 实现类
 *
 * @author yshop
 */
@Slf4j
@Service
@Validated
public class AppStoreOrderServiceImpl extends ServiceImpl<StoreOrderMapper,StoreOrderDO> implements AppStoreOrderService {

    @Resource
    private StoreOrderMapper storeOrderMapper;

    @Resource
    private AppUserAddressService appUserAddressService;
    @Resource
    private MemberUserService userService;
    @Resource
    private AppStoreProductService appStoreProductService;
    @Resource
    private StoreOrderCartInfoService storeOrderCartInfoService;
    @Resource
    private StoreOrderStatusService storeOrderStatusService;
    @Resource
    private UserBillService billService;
    @Resource
    private PayServiceManager manager;
    @Resource
    private WeixinNoticeProducer weixinNoticeProducer;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private StoreProductAttrValueService storeProductAttrValueService;
    @Resource
    private AppStoreShopService appStoreShopService;
    @Resource
    private OrderNumberMapper orderNumberMapper;
    @Resource
    private AppCouponUserService appCouponUserService;
    @Resource
    private AsyncStoreOrderService asyncStoreOrderService;
    @Resource
    private MerchantDetailsService merchantDetailsService;

    @Resource
    private WineStoreServiceImpl wineStoreService;

    @Resource
    private StoreProductService storeProductService;

    @Resource
    private YshopPayWechatParamService wechatParamService;

    private static final String LOCK_KEY = "cart:check:stock:lock";
    private static final String STOCK_LOCK_KEY = "cart:do:stock:lock";

    @Resource
    private WineStoreMapper wineStoreMapper;

    @Resource
    private WeiXinSubscribeService weiXinSubscribeService;

    /**
     * 订单信息
     *
     * @param unique 唯一值或者单号
     * @param uid    用户id
     * @return YxStoreOrderQueryVo
     */
    @Override
    public AppStoreOrderQueryVo getOrderInfo(String unique, Long uid) {
        LambdaQueryWrapper<StoreOrderDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(
                i -> i.eq(StoreOrderDO::getOrderId, unique).or().eq(StoreOrderDO::getUnique, unique).or()
                        .eq(StoreOrderDO::getExtendOrderId, unique));
        if (uid != null) {
            wrapper.eq(StoreOrderDO::getUid, uid);
        }

        AppStoreOrderQueryVo appStoreOrderQueryVo = StoreOrderConvert.INSTANCE.convert1(storeOrderMapper.selectOne(wrapper));
        return appStoreOrderQueryVo;
    }


    /**
     * 创建订单
     *
     * @param uid 用户uid
     * @param param    param
     * @return YxStoreOrder
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Map<String, Object> createOrder(Long uid,  AppOrderParam param) {
        if(OrderLogEnum.ORDER_TAKE_DESK.getValue().equals(param.getOrderType())
                && StrUtil.isBlank(param.getDeskNumber())){
            throw exception(STORE_ORDER_DESK_NOT);
        }
        //转换参数
        List<String> productIds = param.getProductId();
        List<String> numbers = param.getNumber();
        List<String> specs = param.getSpec();


        Integer totalNum = 0;
        List<String> cartIds = new ArrayList<>();

        StoreShopDO storeShopDO = appStoreShopService.getById(param.getShopId());

        BigDecimal sumPrice = BigDecimal.ZERO;
        BigDecimal couponPrice = BigDecimal.ZERO;
        BigDecimal postagePrice = storeShopDO.getDeliveryPrice();
        BigDecimal deductionPrice = BigDecimal.ZERO;

        //对库存检查加锁
        RLock lock = redissonClient.getLock(LOCK_KEY);
        if (lock.tryLock()){
            try {
                for (int i = 0;i < productIds.size();i++){
                    String newSku = StrUtil.replace(specs.get(i),"|",",");
                    appStoreProductService.checkProductStock(uid, Long.valueOf(productIds.get(i)),
                            Integer.valueOf(numbers.get(i)), newSku);
                    totalNum += Integer.valueOf(numbers.get(i));

                    StoreProductAttrValueDO storeProductAttrValue = storeProductAttrValueService
                            .getOne(Wrappers.<StoreProductAttrValueDO>lambdaQuery()
                                    .eq(StoreProductAttrValueDO::getSku, newSku)
                                    .eq(StoreProductAttrValueDO::getProductId, productIds.get(i)));

                    sumPrice = NumberUtil.add(sumPrice, NumberUtil.mul(numbers.get(i),
                            storeProductAttrValue.getPrice().toString()));
                }

            }catch (Exception ex) {
                log.error("[checkProductStock][执行异常]", ex);
                throw exception(new ErrorCode(999999,ex.getMessage()));
            } finally {
                lock.unlock();
            }
        }

        //计算优惠券价格
        if(param.getCouponIdList() != null && param.getCouponIdList().size() > 0){
            if(storeShopDO.getCouponUseNumLimit() != 0 && storeShopDO.getCouponUseNumLimit() < param.getCouponIdList().size()){
                throw exception(STORE_COUPON_NUMBER_LIMIT);
            }
            LocalDateTime now = LocalDateTime.now();

            for(String couponId :param.getCouponIdList()){
                CouponUserDO couponUserDO = appCouponUserService.getById(couponId);
                if(couponUserDO != null){
                    if(couponUserDO.getLeast().compareTo(sumPrice) > 0) {
                        throw exception(COUPON_NOT_CONDITION);
                    }
                    if(couponUserDO.getStatus() == 2 || couponUserDO.getEndTime().isBefore(now)){
                        throw exception(COUPON_INVALID);
                    }
                    if(couponUserDO.getStatus() == 1){
                        throw exception(COUPON_NOT_AVAILABLE);
                    }
                    couponPrice = couponPrice.add(couponUserDO.getValue());

                    //使用了优惠券扣优惠券
                    couponUserDO.setStatus(ShopCommonEnum.IS_STATUS_1.getValue());
                    appCouponUserService.updateById(couponUserDO);

                }
            }

            if(BigDecimal.ZERO.compareTo(storeShopDO.getCouponUseAmountLimit()) != 0 && couponPrice.compareTo(storeShopDO.getCouponUseAmountLimit()) > 0){
                throw exception(STORE_COUPON_AMOUNT_LIMIT);
            }

        }


        BigDecimal payPrice = BigDecimal.ZERO;
        //计算最终支付价格
        if(OrderLogEnum.ORDER_TAKE_OUT.getValue().equals(param.getOrderType())){
            payPrice = NumberUtil.sub(NumberUtil.add(sumPrice,postagePrice),couponPrice,deductionPrice);
        }else{
            payPrice = NumberUtil.sub(sumPrice,couponPrice,deductionPrice);
        }
        if(payPrice.compareTo(BigDecimal.ZERO) == -1){
            payPrice = BigDecimal.ZERO;
        }

        //计算奖励积分
        BigDecimal gainIntegral = this.getGainIntegral(productIds);

        StoreOrderDO storeOrder = new StoreOrderDO();
        //生成分布式唯一值
        String orderSn = IdUtil.getSnowflake(0, 0).nextIdStr();

        //1-现点，2-寄存
        if( "1".equals(param.getDeskType())){
            //添加取餐表
            OrderNumberDO orderNumberDO = OrderNumberDO.builder().orderId(orderSn).build();
            orderNumberMapper.insert(orderNumberDO);

            //组合数据
            LocalDateTime localDateTime = LocalDateTime.now();
            storeOrder.setGetTime(localDateTime.plusMinutes(param.getGettime()));
            storeOrder.setNumberId(orderNumberDO.getId());
            storeOrder.setShopId(storeShopDO.getId());
            storeOrder.setShopName(storeShopDO.getName());
            storeOrder.setUid(uid);
            storeOrder.setOrderId(orderSn);
            //处理如果是外卖 地址
            if(OrderLogEnum.ORDER_TAKE_OUT.getValue().equals(param.getOrderType())){
                if (StrUtil.isEmpty(param.getAddressId())) {
                    throw exception(SELECT_ADDRESS);
                }
                UserAddressDO userAddress = appUserAddressService.getById(param.getAddressId());
                if (ObjectUtil.isNull(userAddress)) {
                    throw exception(USER_ADDRESS_NOT_EXISTS);
                }
                storeOrder.setRealName(userAddress.getRealName());
                storeOrder.setUserPhone(userAddress.getPhone());
                storeOrder.setUserAddress(userAddress.getAddress() + " " + userAddress.getDetail());
            }
            storeOrder.setCartId(StrUtil.join(",", cartIds));
            storeOrder.setTotalNum(totalNum);
            storeOrder.setTotalPrice(sumPrice);
            storeOrder.setTotalPostage(storeShopDO.getDeliveryPrice());

            storeOrder.setCouponIdList(param.getCouponIdList() != null && param.getCouponIdList().size() > 0 ? String.join(",",param.getCouponIdList()):"");
            storeOrder.setCouponPrice(couponPrice);
            storeOrder.setPayPrice(payPrice);
            storeOrder.setPayPostage(storeShopDO.getDeliveryPrice());
            storeOrder.setDeductionPrice(deductionPrice);
            storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_0.getValue());
            storeOrder.setPayType(param.getPayType());
            storeOrder.setUseIntegral(0);
            storeOrder.setBackIntegral(0);
            storeOrder.setGainIntegral(gainIntegral);
            storeOrder.setMark(param.getRemark());
            storeOrder.setCost(BigDecimal.ZERO);
            //storeOrder.setUnique(key);
            storeOrder.setShippingType(OrderInfoEnum.SHIPPIING_TYPE_1.getValue());
            storeOrder.setOrderType(param.getOrderType());
            storeOrder.setDeskNumber(param.getDeskNumber());

            boolean res = this.save(storeOrder);
            if (!res) {
                throw exception(ORDER_GEN_FAIL);
            }

            //保存购物车商品信息，异步执行
            storeOrderCartInfoService.saveCartInfo(storeOrder.getId(), storeOrder.getOrderId(),productIds,numbers,specs);

            //增加订单状态
            storeOrderStatusService.create(uid,storeOrder.getId(), OrderLogEnum.CREATE_ORDER.getValue(),
                    OrderLogEnum.CREATE_ORDER.getDesc());
        }else{
            MemberUserDO memberUserDO = userService.getUser(uid);
            orderSn = "JC" + orderSn;
            WineStoreDO storeDO = null;
            String coupList = param.getCouponIdList() != null && param.getCouponIdList().size() > 0 ? String.join(",",param.getCouponIdList()):"";
//            BigDecimal averagePrice = payPrice.divide(new BigDecimal(param.getProductId().size()), 2 , RoundingMode.HALF_UP);
            for(int i = 0; i < param.getProductId().size(); i++){
                String prodId = param.getProductId().get(i);
                AppWineStoreParam storeParam = new AppWineStoreParam();
                storeParam.setUserId(uid);
                storeParam.setProductId(Long.valueOf(prodId));
                storeParam.setRealName(memberUserDO.getNickname());
                storeParam.setPhone(memberUserDO.getMobile());
                storeParam.setNum(Integer.valueOf(param.getNumber().get(i)));
                storeParam.setSpec(specs.get(i));
                storeParam.setTotalPrice(sumPrice);
                storeParam.setCouponPrice(couponPrice);
                storeParam.setActualPayPrice(payPrice);
                storeParam.setStoreNo(orderSn);
                storeParam.setCouponIdList(coupList);
                storeParam.setPayType(param.getPayType());
                storeDO = wineStoreService.submitStore(storeParam);
            }

            if(storeDO != null){
                //增加订单状态
                storeOrderStatusService.create(uid,storeDO.getId(), OrderLogEnum.PRODCUCT_DEPOSIT.getValue(),
                        OrderLogEnum.PRODCUCT_DEPOSIT.getDesc());
            }
        }


        // 减库存加销量
        this.deStockIncSale(productIds,numbers,specs);

        ////todo 桌面点餐功能 商业版本才有 官网地址：https://www.yixiang.co异步更新桌面信息

        //堂食点餐不需要
        if(!OrderLogEnum.ORDER_TAKE_DESK.getValue().equals(param.getOrderType())) {
            //加入延时队列，10分钟自动取消
            try {
                RBlockingDeque<Object> blockingDeque = redissonClient.getBlockingDeque(ShopConstants.REDIS_ORDER_OUTTIME_UNPAY_QUEUE );
                RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
                delayedQueue.offer(OrderMsg.builder().orderId(orderSn).build(), ShopConstants.ORDER_OUTTIME_UNPAY, TimeUnit.MINUTES);
                String s = TimeUnit.SECONDS.toSeconds(ShopConstants.ORDER_OUTTIME_UNPAY) + "分钟";
                log.info("添加延时队列成功 ，延迟时间：" + s + "订单编号：" + orderSn);
            } catch (Exception e) {
                log.error("添加延时队列失败：{}",e.getMessage());
            }
        }

        Map<String,Object> map = new HashMap<>();
        map.put("orderId",orderSn);
        return map;
    }

    /**
     * 第三方支付
     * @param uid  用户id
     * @param param 订单参数
     * @return
     */
    @Override
    public Map<String, Object> pay(Long uid, AppPayParam param) {
        AppStoreOrderQueryVo orderInfo = null;
        UserBillDO userBillDO =  billService.getOne(new LambdaQueryWrapper<UserBillDO>().eq(UserBillDO::getUid,uid)
                .eq(UserBillDO::getExtendField,param.getUni()));

        List<WineStoreDO> storeDOList = null;

        //1-现点，2-寄存
        if( StringUtils.isNotBlank(param.getUni()) && param.getUni().indexOf("JC") > -1){
            storeDOList = wineStoreService.getStoreByStoreNo(param.getUni());
            if(storeDOList.isEmpty()){
                throw exception(STORE_DEPOSIT_NOT_EXISTS);
            }
        }else{

            orderInfo = getOrderInfo(param.getUni(), uid);
            if (ObjectUtil.isNull(orderInfo) && ObjectUtil.isNull(userBillDO)) {
                throw exception(STORE_ORDER_NOT_EXISTS);
            }
            if(ObjectUtil.isNotNull(orderInfo) && orderInfo.getPaid().equals(OrderInfoEnum.PAY_STATUS_1.getValue())) {
                throw exception(ORDER_PAY_FINISH);
            }
        }


        if(ObjectUtil.isNotNull(userBillDO) && userBillDO.getStatus().equals(OrderInfoEnum.PAY_STATUS_1.getValue())) {
            throw exception(ORDER_PAY_FINISH);
        }

        MemberUserDO memberUserDO = userService.getUser(uid);
        Map<String, Object> map = new LinkedHashMap<>();
        BigDecimal price = BigDecimal.ZERO;
        String msg = "";
        String detailsId = "";
        String orderId = "";
        if(orderInfo != null) {
            price = orderInfo.getPayPrice();
            orderId = orderInfo.getOrderId();
            msg = "商品购买";
        }else if(userBillDO != null){
            //todo 充值商业版本才有 官网购买：https://www.yixiang.co

        }else if(storeDOList.size() > 0){
            price = storeDOList.get(0).getActualPayPrice();

            msg = "商品寄存";
            orderId = storeDOList.get(0).getStoreNo();
            //
        }
        switch (PayTypeEnum.toType(param.getPaytype())){
            case WEIXIN:
                if(AppFromEnum.H5.getValue().equals(param.getFrom())){
                    detailsId = PayIdEnum.WX_WECHAT.getValue();
                    //todo 如果启用微信H5支付充值 需要另外增加一个配置用于同步跳转页面 比如下面的增加了一个id=5的配置,微信支付公众号与H%配置是一样 基本
//                    if(orderInfo != null) {
//                        detailsId = "4";
//                    }else{
//                        detailsId = "5";
//                    }
                    MerchantPayOrder payOrder = new MerchantPayOrder(detailsId, "MWEB", msg,
                            msg, price, param.getUni());

                    Map<String, Object> payOrderInfo = manager.getOrderInfo(payOrder);
                    MerchantDetailsDO merchantDetailsDO = merchantDetailsService.getMerchantDetails(detailsId);
                    String url = merchantDetailsDO.getReturnUrl();

                    String newUrl = "";
                    try {
                        newUrl =  String.format("%s%s", payOrderInfo.get("mweb_url"), "&redirect_url=" + URLEncoder.encode(url,"UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        log.error(e.getMessage());
                    }
                    map.put("data",newUrl);
                    map.put("trade_type","MWEB");
                } else if(AppFromEnum.WECHAT.getValue().equals(param.getFrom())){//微信公众号
//                    MerchantPayOrder payOrder = new MerchantPayOrder("4", "JSAPI", msg,
//                            msg, price, param.getUni());
                    MerchantPayOrder payOrder = new MerchantPayOrder(PayIdEnum.WX_WECHAT.getValue(), "JSAPI", msg,
                            msg, price, param.getUni());
                    payOrder.setOpenid(memberUserDO.getOpenid());
                    map.put("data",manager.getOrderInfo(payOrder));
                    map.put("trade_type","W-JSAPI");
                } else {//微信小程序
                    MerchantPayOrder payOrder = new MerchantPayOrder(PayIdEnum.WX_MINIAPP.getValue(), "JSAPI", msg,
                            msg, price, param.getUni());
                    payOrder.setOpenid(memberUserDO.getOpenid());
                    Map<String, Object>  dataMap = manager.getOrderInfo(payOrder);
                    map.put("data",dataMap);
                    map.put("trade_type","JSAPI");

                    YshopPayWechatDO wechatDO = new YshopPayWechatDO();
                    wechatDO.setAppId((String) dataMap.get("appId"));
                    wechatDO.setNonceStr((String) dataMap.get("nonceStr"));
                    wechatDO.setPackageVal((String) dataMap.get("package"));
                    wechatDO.setPaySign((String) dataMap.get("paySign"));
                    wechatDO.setSignType((String) dataMap.get("signType"));
                    wechatDO.setTimeStamp((String) dataMap.get("timeStamp"));
                    wechatDO.setOrderId(orderId);

                    wechatDO.setCreator(String.valueOf(memberUserDO.getId()));
                    wechatDO.setCreateTime(LocalDateTime.now());
                    wechatDO.setUpdater(String.valueOf(memberUserDO.getId()));
                    wechatDO.setUpdateTime(LocalDateTime.now());
                    wechatParamService.addInfo(wechatDO);
                }
                break;
            case YUE:
                this.yuePay(param.getUni(), uid, storeDOList);
                map.put("status","ok");
                break;
            case ALI:
                //h5支付
                detailsId = PayIdEnum.ALI_H5.getValue();
                //todo 如果启用支付宝H5支付充值 需要另外增加一个配置用于同步跳转页面 比如下面的增加了一个id=6的配置
//                if(orderInfo != null) {
//                    detailsId = "1";
//                }else{
//                    detailsId = "6";
//                }
                MerchantPayOrder payOrder = new MerchantPayOrder(detailsId, "WAP", msg,
                        msg, price, param.getUni());
                map.put("data",manager.toPay(payOrder));

            default:
        }
        return map;
    }

    /**
     * 余额支付
     *
     * @param orderId 订单号
     * @param uid     用户id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void yuePay(String orderId, Long uid, List<WineStoreDO> storeDOList) {
        BigDecimal price = BigDecimal.ZERO;
        if( StringUtils.isNotBlank(orderId) && orderId.indexOf("JC") > -1) {
            WineStoreDO wineStoreDO = storeDOList.get(0);
            price = wineStoreDO.getActualPayPrice();
        }else{
            AppStoreOrderQueryVo orderInfo = getOrderInfo(orderId, uid);
            if (ObjectUtil.isNull(orderInfo)) {
                throw exception(STORE_ORDER_NOT_EXISTS);
            }

            if (OrderInfoEnum.PAY_STATUS_1.getValue().equals(orderInfo.getPaid())) {
                throw exception(ORDER_PAY_FINISH);
            }
            price = orderInfo.getPayPrice();
        }

        AppUserQueryVo userInfo = userService.getAppUser(uid);

        if (userInfo.getNowMoney().compareTo(price) < 0) {
            throw exception(PAY_YUE_NOT);
        }

        userService.decPrice(uid, price);

        //支付成功后处理
        this.paySuccess(orderId, PayTypeEnum.YUE.getValue());
    }


    /**
     * 支付成功后操作
     *
     * @param orderId 订单号
     * @param payType 支付方式
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @TenantIgnore
    public void paySuccess(String orderId, String payType) {
        //处理充值与会员卡订单
        UserBillDO userBillDO =  billService.getOne(new LambdaQueryWrapper<UserBillDO>()
                .eq(UserBillDO::getExtendField,orderId));
        if(userBillDO != null) {
            userBillDO.setStatus(ShopCommonEnum.IS_STATUS_1.getValue());
            billService.updateById(userBillDO);
            if(BillDetailEnum.TYPE_1.getValue().equals(userBillDO.getType())){
                //充值
                userService.incMoney(userBillDO.getUid(), userBillDO.getNumber());
            }

            return;
        }
        //orderId 开头是JC为寄存订单，否则按下单处理
        if(orderId.indexOf("JC") > -1){
            handlePayJC(orderId, payType);
        }else{
            handlePayOrder(orderId, payType);
        }
    }

    private void handlePayJC(String storeNo, String payType){
        List<WineStoreDO> storeDOList = wineStoreService.getStoreByStoreNo(storeNo);
        if(storeDOList.isEmpty()){
            return;
        }
        storeDOList.stream().forEach(info -> {
            info.setStoreStatus(2);
            info.setUpdateTime(LocalDateTime.now());
        });
        wineStoreService.updateBatchById(storeDOList);

        addStatusAndLog(storeDOList.get(0).getUserId(), storeDOList.get(0).getId(),payType, storeDOList.get(0).getActualPayPrice(), "寄存商品");

    }

    /**
     * 扫码点单支付成功处理
     * @param orderId
     * @param payType
     */
    private void handlePayOrder(String orderId, String payType){

        log.info("orderId:[{}]",orderId);
        AppStoreOrderQueryVo orderInfo = getOrderInfo(orderId, null);
        log.info("orderInfo:[{}]",orderInfo);
        if(orderInfo == null){
            return;
        }

        //更新订单状态
        LambdaQueryWrapper<StoreOrderDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoreOrderDO::getOrderId, orderId);
        StoreOrderDO storeOrder = new StoreOrderDO();
        storeOrder.setPaid(OrderInfoEnum.PAY_STATUS_1.getValue());
        storeOrder.setPayType(payType);
        storeOrder.setPayTime(LocalDateTime.now());

        this.update(storeOrder, wrapper);

        //增加用户购买次数
        userService.incPayCount(orderInfo.getUid());

        addStatusAndLog(orderInfo.getUid(), Long.valueOf(orderInfo.getOrderId()),payType, orderInfo.getPayPrice(), "购买商品");


        //发送消息队列进行推送消息,堂食不需要
//        if(!OrderLogEnum.ORDER_TAKE_DESK.getValue().equals(orderInfo.getOrderType()) &&
//                userInfo.getLoginType().equals(AppFromEnum.ROUNTINE.getValue())){
//        List<StoreOrderCartInfoDO> storeOrderCartInfoDOList = storeOrderCartInfoService
//                .list(new LambdaQueryWrapper<StoreOrderCartInfoDO>()
//                        .eq(StoreOrderCartInfoDO::getOid,orderInfo.getId()));
//        List<String> names = storeOrderCartInfoDOList.stream().map(StoreOrderCartInfoDO::getTitle)
//                .collect(Collectors.toList());
//        String productName = StrUtil.join(",",names);
//        weixinNoticeProducer.sendNoticeMessage(orderInfo.getUid(),WechatTempateEnum.PAY_SUCCESS.getValue(),
//                WechatTempateEnum.SUBSCRIBE.getValue(),orderInfo.getOrderId(),
//                "","","","",orderInfo.getId(),orderInfo.getNumberId(),
//                productName,orderInfo.getShopName());
//        }

    }

    private void addStatusAndLog(Long uid, Long oid, String payType, BigDecimal payPrice, String titleType){
        //增加状态
        storeOrderStatusService.create(uid,oid, OrderLogEnum.PAY_ORDER_SUCCESS.getValue(),
                OrderLogEnum.PAY_ORDER_SUCCESS.getDesc());


        MemberUserDO userInfo = userService.getUser(uid);
        //增加流水
        String payTypeMsg = PayTypeEnum.WEIXIN.getDesc();
        if (PayTypeEnum.YUE.getValue().equals(payType)) {
            payTypeMsg = PayTypeEnum.YUE.getDesc();
        }else if (PayTypeEnum.ALI.getValue().equals(payType)) {
            payTypeMsg = PayTypeEnum.ALI.getDesc();
        }else if(PayTypeEnum.CASH.getValue().equals(payType)){
            payTypeMsg = PayTypeEnum.CASH.getDesc();
        }
        billService.expend(userInfo.getId(), titleType,
                BillDetailEnum.CATEGORY_1.getValue(),
                BillDetailEnum.TYPE_3.getValue(),
                payPrice.doubleValue(), userInfo.getNowMoney().doubleValue(),
                payTypeMsg + payPrice + "元购买商品");
    }

    /**
     * 减库存增加销量
     *
     * @param productIds 商品id
     * @param numbers 商品数量
     * @param specs 商品规格
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deStockIncSale(List<String> productIds,List<String> numbers,List<String> specs) {


        log.info("========减库存增加销量start=========");
        //对库存加锁
        RLock lock = redissonClient.getLock(STOCK_LOCK_KEY);
        if (lock.tryLock()) {
            try {
                for (int i = 0;i < productIds.size();i++){
                    String newSku = StrUtil.replace(specs.get(i),"|",",");
                    appStoreProductService.decProductStock(Integer.valueOf(numbers.get(i)),
                            Long.valueOf(productIds.get(i)),
                            newSku, 0L, "");
                }
            }catch (Exception ex) {
                log.error("[deStockIncSale][执行异常]", ex);
                throw exception(new ErrorCode(999999,ex.getMessage()));
            } finally {
                lock.unlock();
            }
        }
    }


    /**
     * 订单列表
     *
     * @param uid   用户id
     * @param type  OrderStatusEnum
     * @param page  page
     * @param limit limit
     * @return list
     */
    @Override
    public List<AppStoreOrderQueryVo> orderList(Long uid, int type, int page, int limit) {
        LambdaQueryWrapper<StoreOrderDO> wrapper = new LambdaQueryWrapper<>();
        if (uid != null) {
            wrapper.eq(StoreOrderDO::getUid, uid);
        }
        wrapper.eq(StoreOrderDO::getIsSystemDel,ShopCommonEnum.DELETE_0.getValue()).orderByDesc(StoreOrderDO::getId);
       // wrapper.eq(StoreOrderDO::getPaid, OrderInfoEnum.PAY_STATUS_1.getValue())
                //.eq(StoreOrderDO::getRefundStatus, OrderInfoEnum.REFUND_STATUS_0.getValue())
                //.eq(StoreOrderDO::getStatus, OrderInfoEnum.STATUS_0.getValue())
               // .orderByDesc(StoreOrderDO::getId);
        switch (OrderStatusEnum.toType(type)) {
            case STATUS__1:
                break;
            //未支付
            case STATUS_0:
                wrapper.eq(StoreOrderDO::getPaid, OrderInfoEnum.PAY_STATUS_0.getValue())
                        .eq(StoreOrderDO::getRefundStatus, OrderInfoEnum.REFUND_STATUS_0.getValue())
                        .eq(StoreOrderDO::getStatus, OrderInfoEnum.STATUS_0.getValue());
                break;
            //已经支付
            case STATUS_1:
                wrapper.eq(StoreOrderDO::getPaid, OrderInfoEnum.PAY_STATUS_1.getValue())
                        .eq(StoreOrderDO::getRefundStatus, OrderInfoEnum.REFUND_STATUS_0.getValue())
                        .eq(StoreOrderDO::getStatus, OrderInfoEnum.STATUS_0.getValue());
                break;
            //待收货
            case STATUS_2:
                wrapper.eq(StoreOrderDO::getPaid, OrderInfoEnum.PAY_STATUS_1.getValue())
                        .eq(StoreOrderDO::getRefundStatus, OrderInfoEnum.REFUND_STATUS_0.getValue())
                        .eq(StoreOrderDO::getStatus, OrderInfoEnum.STATUS_1.getValue());
                break;
//            //待评价
//            case STATUS_3:
//                wrapper.eq(StoreOrderDO::getPaid, OrderInfoEnum.PAY_STATUS_1.getValue())
//                        .eq(StoreOrderDO::getRefundStatus, OrderInfoEnum.REFUND_STATUS_0.getValue())
//                        .eq(StoreOrderDO::getStatus, OrderInfoEnum.STATUS_2.getValue());
//                break;
            //已完成
            case STATUS_4:
                wrapper.eq(StoreOrderDO::getPaid, OrderInfoEnum.PAY_STATUS_1.getValue())
                        .eq(StoreOrderDO::getRefundStatus, OrderInfoEnum.REFUND_STATUS_0.getValue())
                        .ge(StoreOrderDO::getStatus, OrderInfoEnum.STATUS_2.getValue());
                break;
            //退款
            case STATUS_MINUS_3:
                String[] strs = {"1", "2"};
                wrapper.in(StoreOrderDO::getRefundStatus, Arrays.asList(strs));
                break;
            default:
        }

        Page<StoreOrderDO> pageModel = new Page<>(page, limit);
        IPage<StoreOrderDO> pageList = storeOrderMapper.selectPage(pageModel, wrapper);
        List<AppStoreOrderQueryVo> list = StoreOrderConvert.INSTANCE.convertList01(pageList.getRecords());

        return list.stream().map(this::handleOrder).collect(Collectors.toList());

    }


    /**
     * 处理订单返回的状态
     *
     * @param order order
     * @return YxStoreOrderQueryVo
     */
    @Override
    public AppStoreOrderQueryVo handleOrder(AppStoreOrderQueryVo order) {
        LambdaQueryWrapper<StoreOrderCartInfoDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoreOrderCartInfoDO::getOid, order.getId());
        List<StoreOrderCartInfoDO> cartInfos = storeOrderCartInfoService.list(wrapper);

        order.setCartInfo(cartInfos);

        StoreShopDO storeShopDO = appStoreShopService.getById(order.getShopId());
        order.setShop(StoreShopConvert.INSTANCE.convert02(storeShopDO));

        long count = storeOrderMapper.selectCount(new LambdaQueryWrapper<StoreOrderDO>()
                .eq(StoreOrderDO::getShopId,order.getShopId())
                .lt(StoreOrderDO::getCreateTime,order.getCreateTime())
                .eq(StoreOrderDO::getPaid, OrderInfoEnum.PAY_STATUS_1.getValue())
                .eq(StoreOrderDO::getRefundStatus, OrderInfoEnum.REFUND_STATUS_0.getValue())
                .eq(StoreOrderDO::getStatus, OrderInfoEnum.STATUS_0.getValue()));
        order.setPreNum(count);


        StatusDto statusDTO = new StatusDto();
        if (OrderStatusEnum.STATUS_0.getValue().equals(order.getPaid())) {
            //计算未支付到自动取消订 时间
            int offset = Integer.valueOf(String.valueOf(ShopConstants.ORDER_OUTTIME_UNPAY));
            //Date time = DateUtil.offsetMinute(order.getCreateTime()., offset);
            statusDTO.setYClass("nobuy");
            //statusDTO.setMsg(StrUtil.format("请在{}前完成支付", DateUtil.formatDateTime(time)));
            statusDTO.setType("0");
            statusDTO.setTitle("未支付");
        } else if (OrderInfoEnum.REFUND_STATUS_1.getValue().equals(order.getRefundStatus())) {
            statusDTO.setYClass("state-sqtk");
            statusDTO.setMsg("商家审核中,请耐心等待");
            statusDTO.setType("-1");
            statusDTO.setTitle("申请退款中");
        } else if (OrderInfoEnum.REFUND_STATUS_2.getValue().equals(order.getRefundStatus())) {
            statusDTO.setYClass("state-sqtk");
            statusDTO.setMsg("已为您退款,感谢您的支持");
            statusDTO.setType("-2");
            statusDTO.setTitle("已退款");
        } else if (OrderInfoEnum.STATUS_0.getValue().equals(order.getStatus())) {
            // 拼团 todo
            if (order.getPinkId() > 0) {
            } else {
                statusDTO.setYClass("state-nfh");
                statusDTO.setMsg("商家未发货,请耐心等待");
                statusDTO.setType("1");
                statusDTO.setTitle("制作中");
            }

        } else if (OrderInfoEnum.STATUS_1.getValue().equals(order.getStatus())) {
            statusDTO.setTitle("制作中");
            statusDTO.setYClass("state-ysh");
            statusDTO.setMsg("服务商已发货");
            statusDTO.setType("2");

        } else if (OrderInfoEnum.STATUS_2.getValue().equals(order.getStatus())) {
            statusDTO.setTitle("送餐中");
            statusDTO.setYClass("state-ypj");
            statusDTO.setMsg("已收货,快去评价一下吧");
            statusDTO.setType("3");
        } else if (OrderInfoEnum.STATUS_3.getValue().equals(order.getStatus())) {
            statusDTO.setYClass("state-ytk");
            statusDTO.setMsg("交易完成,感谢您的支持");
            statusDTO.setType("4");
            statusDTO.setTitle("交易完成");
        }

        if (PayTypeEnum.WEIXIN.getValue().equals(order.getPayType())) {
            statusDTO.setPayType("微信支付");
        } else if (PayTypeEnum.YUE.getValue().equals(order.getPayType())) {
            statusDTO.setPayType("余额支付");
        } else if (PayTypeEnum.ALI.getValue().equals(order.getPayType())) {
            statusDTO.setPayType("支付宝支付");
        }else if (PayTypeEnum.JC.getValue().equals(order.getPayType())) {
            statusDTO.setPayType(PayTypeEnum.JC.getDesc());
        }else {
            statusDTO.setPayType("积分支付");
        }

        order.setStatusDto(statusDTO);


        return order;
    }


    /**
     * 订单确认收货
     *
     * @param orderId 单号
     * @param uid     uid
     */
    @Override
    public void takeOrder(String orderId, Long uid) {
        AppStoreOrderQueryVo order = this.getOrderInfo(orderId, uid);
        if (ObjectUtil.isNull(order)) {
            throw exception(STORE_ORDER_NOT_EXISTS);
        }

        if (OrderInfoEnum.PAY_STATUS_0.getValue().equals(order.getPaid())) {
            throw exception(ORDER_STATUS_ERROR);
        }

        if (OrderInfoEnum.STATUS_3.getValue().equals(order.getStatus())){
            throw exception(ORDER_STATUS_FINISH);
        }
        order = handleOrder(order);
//        if (order.getOrderType().equals(OrderLogEnum.ORDER_TAKE_OUT.getValue())
//                && !OrderStatusEnum.STATUS_2.getValue().toString().equals(order.get_status().get_type())) {
//            throw exception(ORDER_STATUS_ERROR);
//        }

        if(PayTypeEnum.JC.getValue().equals(order.getPayType())){
            WineStoreDO storeDO = wineStoreMapper.selectById(order.getRemark());
            if(storeDO == null || storeDO.getStoreStatus() != 4){
                throw exception(STORE_DEPOSIT_NOT_EXISTS);
            }
            storeDO.setStoreStatus(5);
            storeDO.setUpdateTime(LocalDateTime.now());
            wineStoreMapper.updateById(storeDO);
        }

        StoreOrderDO storeOrder = new StoreOrderDO();
        storeOrder.setStatus(OrderInfoEnum.STATUS_3.getValue());
        storeOrder.setId(order.getId());
        this.updateById(storeOrder);

        //增加状态
        storeOrderStatusService.create(order.getUid(),order.getId(), OrderLogEnum.TAKE_ORDER_DELIVERY.getValue(), OrderLogEnum.TAKE_ORDER_DELIVERY.getDesc());

        //奖励积分
        this.gainUserIntegral(order);


        //分销计算 todo

    }

    @Override
    public void orderConfirm(String orderId, Long uid) {
        AppStoreOrderQueryVo order = this.getOrderInfo(orderId, uid);
        if (ObjectUtil.isNull(order)) {
            throw exception(STORE_ORDER_NOT_EXISTS);
        }

        if (OrderInfoEnum.PAY_STATUS_0.getValue().equals(order.getPaid())) {
            throw exception(ORDER_STATUS_ERROR);
        }

        if (OrderInfoEnum.STATUS_3.getValue().equals(order.getStatus())){
            throw exception(ORDER_STATUS_FINISH);
        }
        order = handleOrder(order);


        StoreOrderDO storeOrder = new StoreOrderDO();
        storeOrder.setStatus(OrderInfoEnum.STATUS_1.getValue());
        storeOrder.setId(order.getId());
        this.updateById(storeOrder);

        //增加状态
        storeOrderStatusService.create(order.getUid(),order.getId(), OrderLogEnum.TAKE_ORDER_DELIVERY.getValue(), OrderLogEnum.TAKE_ORDER_DELIVERY.getDesc());

    }

    @Override
    public void pendingReceipt(String orderId, Long uid) {
        AppStoreOrderQueryVo order = this.getOrderInfo(orderId, uid);
        if (ObjectUtil.isNull(order)) {
            throw exception(STORE_ORDER_NOT_EXISTS);
        }

        if (OrderInfoEnum.PAY_STATUS_0.getValue().equals(order.getPaid())) {
            throw exception(ORDER_STATUS_ERROR);
        }

        if (OrderInfoEnum.STATUS_3.getValue().equals(order.getStatus())){
            throw exception(ORDER_STATUS_FINISH);
        }
        order = handleOrder(order);


        StoreOrderDO storeOrder = new StoreOrderDO();
        storeOrder.setStatus(OrderInfoEnum.STATUS_2.getValue());
        storeOrder.setId(order.getId());
        this.updateById(storeOrder);

        //增加状态
        storeOrderStatusService.create(order.getUid(),order.getId(), OrderLogEnum.TAKE_ORDER_DELIVERY.getValue(), OrderLogEnum.TAKE_ORDER_DELIVERY.getDesc());

    }

    /**
     * 申请退款
     *
     * @param explain 退款备注
     * @param Img     图片
     * @param text    理由
     * @param orderId 订单号
     * @param uid     uid
     */
    @Override
    public void orderApplyRefund(String explain, String Img, String text, String orderId, Long uid) {
        AppStoreOrderQueryVo order = this.getOrderInfo(orderId, uid);
        if (ObjectUtil.isNull(order)) {
            throw exception(STORE_ORDER_NOT_EXISTS);
        }

        if (OrderInfoEnum.REFUND_STATUS_2.getValue().equals(order.getRefundStatus())) {
            throw exception(ORDER_REFUNDED);
        }
        if (OrderInfoEnum.REFUND_STATUS_1.getValue().equals(order.getRefundStatus())) {
            throw exception(ORDER_REFUNDING);
        }


        StoreOrderDO storeOrder = new StoreOrderDO();
        storeOrder.setRefundStatus(OrderInfoEnum.REFUND_STATUS_1.getValue());
        storeOrder.setRefundReasonTime(LocalDateTime.now());
        storeOrder.setRefundReasonWapExplain(explain);
        storeOrder.setRefundReasonWapImg(Img);
        storeOrder.setRefundReasonWap(text);
        storeOrder.setId(order.getId());
        this.updateById(storeOrder);

        //增加状态
        storeOrderStatusService.create(order.getUid(),order.getId(),
                OrderLogEnum.REFUND_ORDER_APPLY.getValue(),
                "用户申请退款，原因：" + text);

        //todo 消息推送
        weiXinSubscribeService.sendMsgToAdmin(order.getOrderId());
    }



    /**
     * 删除订单
     *
     * @param orderId 单号
     * @param uid     uid
     */
    @Override
    public void removeOrder(String orderId, Long uid) {
        AppStoreOrderQueryVo order = this.getOrderInfo(orderId,  uid);
        if (order == null) {
            throw exception(STORE_ORDER_NOT_EXISTS);
        }
        order = handleOrder(order);
        if (!OrderInfoEnum.STATUS_3.getValue().equals(order.getStatus())) {
            throw exception(ORDER_NOT_DELETE);
        }

        this.removeById(order.getId());

        //增加状态
        storeOrderStatusService.create(uid,order.getId(),
                OrderLogEnum.REMOVE_ORDER.getValue(),
                OrderLogEnum.REMOVE_ORDER.getDesc());
    }

    /**
     * 未付款取消订单
     *
     * @param orderId 订单号
     * @param uid     用户id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void cancelOrder(String orderId, Long uid) {
        log.info("订单取消：({})",orderId);
        /**
         * 增加寄存取消
         */
        if(orderId.indexOf("JC") > -1){

            List<WineStoreDO> storeDOList = wineStoreService.getStoreByStoreNo(orderId).stream().filter(info -> info.getStoreStatus() == 1).collect(Collectors.toList());
            if(storeDOList.isEmpty()){
                log.info("该{}订单已支付，不须取消.............",orderId);
                return;
            }
            storeDOList.stream().forEach(info -> {
                info.setUpdateTime(LocalDateTime.now());
                info.setDeleted(1);
            });
            wineStoreService.updateBatchById(storeDOList);
            return;
        }

        AppStoreOrderQueryVo order = this.getOrderInfo(orderId, uid);
        if (ObjectUtil.isNull(order)) {
            throw exception(STORE_ORDER_NOT_EXISTS);
        }
        if (order.getPaid() != 0) {
            throw exception(ORDER_NOT_CANCEL);
        }


        this.regressionStock(order);

        this.regressionCoupon(order, 0);

        storeOrderMapper.deleteById(order.getId());
    }


    /**
     * 退回积分
     *
     * @param order 订单
     */
    private void regressionIntegral(AppStoreOrderQueryVo order, Integer type) {
        if (OrderInfoEnum.PAY_STATUS_1.getValue().equals(order.getPaid())
                || OrderStatusEnum.STATUS_MINUS_2.getValue().equals(order.getStatus())) {
            return;
        }

        if (order.getPayIntegral().compareTo(0) > 0) {
            order.setUseIntegral(order.getPayIntegral());
        }
        if (order.getUseIntegral().compareTo(0) <= 0) {
            return;
        }

        if (!OrderStatusEnum.STATUS_MINUS_2.getValue().equals(order.getStatus())
                && !OrderInfoEnum.REFUND_STATUS_2.getValue().equals(order.getRefundStatus())
                && order.getBackIntegral().compareTo(0) > 0) {
            return;
        }

        MemberUserDO yxUser = userService.getById(order.getUid());

        //增加积分
//        Integer newIntegral = order.getUseIntegral().intValue() + yxUser.getIntegral();
//        yxUser.setIntegral(newIntegral);
        userService.updateById(yxUser);

        //增加流水
        billService.income(yxUser.getId(), "积分回退", BillDetailEnum.CATEGORY_2.getValue(),
                BillDetailEnum.TYPE_8.getValue(),
                order.getUseIntegral().doubleValue(),
                0,
                "购买商品失败,回退积分" + order.getUseIntegral(), order.getId().toString());

        //更新回退积分
        StoreOrderDO storeOrder = new StoreOrderDO();
        storeOrder.setBackIntegral(order.getUseIntegral());
        storeOrder.setId(order.getId());
        this.updateById(storeOrder);
    }

    /**
     * 退回优惠券
     *
     * @param order 订单 todo
     */
    public void regressionCoupon(AppStoreOrderQueryVo order, Integer type) {
        if (OrderInfoEnum.PAY_STATUS_1.getValue().equals(order.getPaid())
                || OrderStatusEnum.STATUS_MINUS_2.getValue().equals(order.getStatus())) {
            return;
        }

        if (StringUtils.isNotBlank(order.getCouponIdList())) {
            List<String> couponIdList = List.of(order.getCouponIdList().split(","));
            for(String couponId: couponIdList){
                CouponUserDO couponUser = appCouponUserService
                        .getOne(Wrappers.<CouponUserDO>lambdaQuery()
                                .eq(CouponUserDO::getId, couponId)
                                .eq(CouponUserDO::getStatus, ShopCommonEnum.IS_STATUS_1.getValue())
                                .eq(CouponUserDO::getUserId, order.getUid()));

                if (ObjectUtil.isNotNull(couponUser)) {
                    couponUser.setStatus(ShopCommonEnum.IS_STATUS_0.getValue());
                    appCouponUserService.updateById(couponUser);
                }
            }

        }
    }

    /**
     * 退回库存
     *
     * @param order 订单
     */
    private void regressionStock(AppStoreOrderQueryVo order) {
        if (OrderInfoEnum.PAY_STATUS_1.getValue().equals(order.getPaid())
                || OrderStatusEnum.STATUS_MINUS_2.getValue().equals(order.getStatus())) {
            return;
        }

        LambdaQueryWrapper<StoreOrderCartInfoDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoreOrderCartInfoDO::getOid, order.getId());

        List<StoreOrderCartInfoDO> cartInfoList = storeOrderCartInfoService.list(wrapper);
        for (StoreOrderCartInfoDO cartInfo : cartInfoList) {
            String newSku = StrUtil.replace(cartInfo.getSpec(),"|",",");
            appStoreProductService.incProductStock(cartInfo.getNumber(), cartInfo.getProductId()
                    ,newSku, 0L, null);
        }
    }
    

    /**
     * 奖励积分
     *
     * @param order 订单
     */
    private void gainUserIntegral(AppStoreOrderQueryVo order) {
        if (order.getGainIntegral().compareTo(0) > 0) {
            MemberUserDO user = userService.getUser(order.getUid());

//            Integer newIntegral = user.getIntegral() + order.getGainIntegral();
//            user.setIntegral(newIntegral);
            user.setId(order.getUid());
            userService.updateById(user);

            //增加流水
            billService.income(user.getId(), "购买商品赠送积分", BillDetailEnum.CATEGORY_2.getValue(),
                    BillDetailEnum.TYPE_9.getValue(),
                    order.getGainIntegral().doubleValue(),
                    0,
                    "购买商品赠送" + 0 + "积分", order.getId().toString());
        }
    }



    /**
     * 计算奖励的积分
     *
     * @param productIds
     * @return double
     */
    private BigDecimal getGainIntegral(List<String> productIds) {
        BigDecimal gainIntegral = BigDecimal.ZERO;
        for (int i = 0;i < productIds.size();i++){
            StoreProductDO storeProductDO = appStoreProductService.getById(productIds.get(i));
            if(storeProductDO.getGiveIntegral().intValue() == 0){
                continue;
            }
            gainIntegral = NumberUtil.add(gainIntegral, storeProductDO.getGiveIntegral());
        }
        return gainIntegral;
    }













}
