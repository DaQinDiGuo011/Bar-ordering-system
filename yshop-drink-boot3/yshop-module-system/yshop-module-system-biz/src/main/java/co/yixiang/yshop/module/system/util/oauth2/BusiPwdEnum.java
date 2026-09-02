package co.yixiang.yshop.module.system.util.oauth2;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum BusiPwdEnum {


    ORDER_REFUND("refund_order_pwd", "订单退款密码"),
    BALANCE_MDF("balance_modify_pwd", "余额修改密码"),
    REGISTER_MDF("register_modify_pwd", "寄存修改密码"),
    RECHARGE_PACKAGE("recharge_package_pwd", "充值套餐修改密码"),
    COUPON_ALLO("coupon_allocation_pwd", "优惠券分配密码");

    /**
     * 匹配
     */
    private final String value;
    /**
     * 匹配的名字
     */
    private final String name;


}
