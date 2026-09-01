package co.yixiang.yshop.module.member.service.user;

import co.yixiang.yshop.framework.common.enums.PayIdEnum;
import co.yixiang.yshop.module.member.controller.app.user.vo.AppRechargeOrderVO;
import co.yixiang.yshop.module.member.controller.app.user.vo.AppRechargePackageVO;
import co.yixiang.yshop.module.member.dal.dataobject.user.MemberUserDO;
import co.yixiang.yshop.module.member.dal.dataobject.user.RechargeOrderDO;
import co.yixiang.yshop.module.member.dal.dataobject.user.RechargePackageDO;
import co.yixiang.yshop.module.member.dal.mysql.user.RechargeOrderMapper;
import co.yixiang.yshop.module.member.dal.mysql.user.RechargePackageMapper;
import co.yixiang.yshop.module.member.dal.mysql.user.UserWalletMapper;
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
import java.util.UUID;
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
        order.setOrderNo(genOrderNo());
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
        userDO.setNowMoney(userDO.getNowMoney().add(order.getRechargeAmount()));

        userService.updateById(userDO);
    }

    @Override
    public RechargeOrderDO getOrderByNo(String orderNo) {
        RechargeOrderDO orderDO = orderMapper.selectByOrderNo(orderNo);
        return orderDO;
    }


    /**
     * 生成订单号
     */
    private String genOrderNo(){
        return "RC" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0,8);
    }
}
