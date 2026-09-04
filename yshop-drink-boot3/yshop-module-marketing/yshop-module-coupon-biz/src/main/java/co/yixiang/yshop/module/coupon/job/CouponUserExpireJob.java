package co.yixiang.yshop.module.coupon.job;

import co.yixiang.yshop.framework.quartz.core.handler.JobHandler;
import co.yixiang.yshop.module.coupon.dal.dataobject.couponuser.CouponUserDO;
import co.yixiang.yshop.module.coupon.dal.mysql.couponuser.CouponUserMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 优惠券到期定时处理
 *
 * 每天凌晨执行一次，将 status = 0 且已过 end_time 的用户优惠券更新为已失效(status = 2)
 */
@Slf4j
@Component
public class CouponUserExpireJob implements JobHandler {

    @Resource
    private CouponUserMapper couponUserMapper;

    @Override
    public String execute(String param) {
        LocalDateTime now = LocalDateTime.now();
        CouponUserDO updateDO = new CouponUserDO();
        updateDO.setStatus(2);
        int count = couponUserMapper.update(updateDO, Wrappers.<CouponUserDO>lambdaUpdate()
                .eq(CouponUserDO::getStatus, 0)
                .lt(CouponUserDO::getEndTime, now));
        log.info("[execute][将已过期的优惠券 ({}) 张更新为已失效]", count);
        return "已将 " + count + " 张过期优惠券更新为失效";
    }

}
