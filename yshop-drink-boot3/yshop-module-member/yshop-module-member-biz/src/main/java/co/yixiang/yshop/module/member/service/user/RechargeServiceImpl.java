package co.yixiang.yshop.module.member.service.user;

import cn.hutool.core.util.IdUtil;
import co.yixiang.yshop.framework.common.enums.PayIdEnum;
import co.yixiang.yshop.framework.common.enums.ShopCommonEnum;
import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.member.controller.admin.recharge.vo.RechargeOrderPageReqVO;
import co.yixiang.yshop.module.member.controller.admin.recharge.vo.RechargePackagePageReqVO;
import co.yixiang.yshop.module.member.controller.admin.recharge.vo.RechargePackageSaveReqVO;
import co.yixiang.yshop.module.member.controller.app.user.vo.AppRechargeOrderVO;
import co.yixiang.yshop.module.member.controller.app.user.vo.AppRechargePackageVO;
import co.yixiang.yshop.module.member.dal.dataobject.user.MemberUserDO;
import co.yixiang.yshop.module.member.dal.dataobject.user.RechargeOrderDO;
import co.yixiang.yshop.module.member.dal.dataobject.user.RechargePackageDO;
import co.yixiang.yshop.module.member.dal.dataobject.userbill.UserBillDO;
import co.yixiang.yshop.module.member.dal.mysql.user.RechargeOrderMapper;
import co.yixiang.yshop.module.member.dal.mysql.user.RechargePackageMapper;
import co.yixiang.yshop.module.member.dal.mysql.user.UserWalletMapper;
import co.yixiang.yshop.module.member.enums.BillDetailEnum;
import co.yixiang.yshop.module.member.service.userbill.UserBillService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.egzosn.pay.spring.boot.core.bean.MerchantPayOrder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Validated
public class RechargeServiceImpl implements RechargeService{

    @Resource
    private RechargePackageMapper packageMapper;
    @Resource
    private RechargeOrderMapper orderMapper;
    @Resource
    private UserWalletMapper walletMapper;

    @Resource
    private MemberUserService userService;
    @Resource
    private UserBillService billService;
    @Resource
    private PayServiceManager manager;
    /**
     * 获取启用套餐列表
     */
    @Override
    public List<AppRechargePackageVO> getPackageList() {
        List<AppRechargePackageVO> list = packageMapper.selectEnableList().stream().map(info -> {
            AppRechargePackageVO packageVO = new AppRechargePackageVO();
            packageVO.setId(info.getId());
            packageVO.setStatus(info.getStatus());
            packageVO.setSort(info.getSort());
            packageVO.setAmount(info.getAmount());
            packageVO.setGiftAmount(info.getGiftAmount());
            packageVO.setGrowValue(info.getGrowValue());
            packageVO.setVipLevel(info.getVipLevel());
            return packageVO;
        }).collect(Collectors.toList());
        return list;
    }

    /**
     * 创建充值订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> createRechargeOrder(AppRechargeOrderVO dto) {
        BigDecimal payAmount = dto.getRechargeAmount();
        if(payAmount == null || payAmount.compareTo(BigDecimal.ZERO) <= 0){
            throw new RuntimeException("充值金额必须大于0");
        }
        MemberUserDO userDO = userService.getUserByUserCode(dto.getOpenId());
        if(userDO == null){
            throw new RuntimeException("用户不存在");
        }

        RechargeOrderDO order = new RechargeOrderDO();
        order.setUserId(userDO.getId());
        String orderSn = IdUtil.getSnowflake(0, 0).nextIdStr();
        order.setOrderNo("RC" + orderSn);
        order.setRechargeAmount(payAmount);
        order.setPayStatus(0);

        // 选择套餐
        if(dto.getPackageId() != null){
            RechargePackageDO pkg = packageMapper.selectById(dto.getPackageId());
            if(pkg == null){
                throw new RuntimeException("套餐不存在");
            }
            order.setPackageId(pkg.getId());
            order.setGiftAmount(pkg.getGiftAmount());
            order.setGiftGrowValue(pkg.getGrowValue());
        }else{
            // 自定义金额，无赠送，你可以自己修改规则
            order.setGiftAmount(BigDecimal.ZERO);
            order.setGiftGrowValue(0);
        }
        orderMapper.insert(order);

        UserBillDO userBillDO = new UserBillDO();
        userBillDO.setUid(userDO.getId());
        userBillDO.setPm(1);
        userBillDO.setTitle("账户充值");
        userBillDO.setCategory("now_money");
        userBillDO.setType(BillDetailEnum.TYPE_1.getValue());
        userBillDO.setNumber(order.getRechargeAmount().add(order.getGiftAmount()));
        userBillDO.setBalance(userDO.getNowMoney().add(order.getRechargeAmount()));
        String mark = "微信支付"+ order.getRechargeAmount() +"元充值账户";
        if(order.getGiftAmount() != null && BigDecimal.ZERO.compareTo(order.getGiftAmount()) < 0){
            mark = mark + "; 并赠送" + order.getGiftAmount() + "元";
        }
        userBillDO.setMark(mark);
        userBillDO.setCreator(String.valueOf(userDO.getId()));
        userBillDO.setCreateTime(LocalDateTime.now());
        userBillDO.setUpdater(String.valueOf(userDO.getId()));
        userBillDO.setUpdateTime(LocalDateTime.now());
        userBillDO.setStatus(ShopCommonEnum.IS_STATUS_0.getValue());
        userBillDO.setExtendField(order.getOrderNo());
        billService.save(userBillDO);

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("orderId", order.getId());
        resultMap.put("orderNo", order.getOrderNo());

        MerchantPayOrder payOrder = new MerchantPayOrder(PayIdEnum.WX_MINIAPP.getValue(), "JSAPI", "充值",
                "充值", payAmount, order.getOrderNo());
        payOrder.setOpenid(dto.getOpenId());
        payOrder.setAddition("RECHARGE");
        Map<String, Object> payMap = new HashMap<>();
        payMap.put("data",manager.getOrderInfo(payOrder));
        payMap.put("trade_type","JSAPI");

        resultMap.put("payParams", payMap);
        return resultMap;
    }

    /**
     * 支付回调：支付成功，更新订单、增加余额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaySuccess(String orderNo) {
        RechargeOrderDO order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            throw new RuntimeException("订单不存在");
        }
        // 已支付直接返回，防止重复回调
        if(order.getPayStatus() == 1){
            return;
        }
        // 更新订单状态
        orderMapper.updatePaySuccess(orderNo, LocalDateTime.now());

        // 钱包余额增加：充值本金+赠送金额
//        BigDecimal totalAdd = order.getRechargeAmount().add(order.getGiftAmount());
//        walletMapper.rechargeBalance(order.getUserId(), totalAdd, order.getGiftGrowValue(), null, null);

        MemberUserDO userDO = userService.getUser(order.getUserId());

        userDO.setNowMoney(userDO.getNowMoney().add(order.getRechargeAmount()).add(order.getGiftAmount()));

        UserBillDO userBillDO =  billService.getOne(new LambdaQueryWrapper<UserBillDO>()
                .eq(UserBillDO::getExtendField, orderNo));
        if(userBillDO != null) {
            userBillDO.setStatus(ShopCommonEnum.IS_STATUS_1.getValue());
            billService.updateById(userBillDO);
        }

        billService.updateById(userBillDO);

    }

    @Override
    public RechargeOrderDO getOrderByNo(String orderNo) {
        RechargeOrderDO orderDO = orderMapper.selectByOrderNo(orderNo);
        return orderDO;
    }

    @Override
    public PageResult<RechargeOrderDO> getRechargeOrderPage(RechargeOrderPageReqVO reqVO) {
        IPage<RechargeOrderDO> page = orderMapper.selectPage(new Page<>(reqVO.getPageNo(), reqVO.getPageSize()), reqVO);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public PageResult<RechargePackageDO> getRechargePackagePage(RechargePackagePageReqVO reqVO) {
        IPage<RechargePackageDO> page = packageMapper.selectPage(new Page<>(reqVO.getPageNo(), reqVO.getPageSize()), reqVO);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public Long createRechargePackage(RechargePackageSaveReqVO dto) {
        RechargePackageDO pkg = new RechargePackageDO();
        pkg.setAmount(dto.getAmount());
        pkg.setGiftAmount(dto.getGiftAmount() == null ? BigDecimal.ZERO : dto.getGiftAmount());
        pkg.setGrowValue(dto.getGrowValue() == null ? 0 : dto.getGrowValue());
        pkg.setVipLevel(dto.getVipLevel());
        pkg.setSort(dto.getSort() == null ? 0 : dto.getSort());
        pkg.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        packageMapper.insert(pkg);
        return pkg.getId();
    }

    @Override
    public void updateRechargePackage(RechargePackageSaveReqVO dto) {
        RechargePackageDO pkg = packageMapper.selectById(dto.getId());
        if(pkg == null){
            throw new RuntimeException("充值套餐不存在");
        }
        pkg.setAmount(dto.getAmount());
        pkg.setGiftAmount(dto.getGiftAmount() == null ? BigDecimal.ZERO : dto.getGiftAmount());
        pkg.setGrowValue(dto.getGrowValue() == null ? 0 : dto.getGrowValue());
        pkg.setVipLevel(dto.getVipLevel());
        pkg.setSort(dto.getSort() == null ? 0 : dto.getSort());
        pkg.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        packageMapper.update(pkg);
    }

}
