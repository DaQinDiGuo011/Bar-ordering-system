package co.yixiang.yshop.module.member.controller.app.user.vo;

import co.yixiang.yshop.module.member.dal.dataobject.user.UserCouponDO;
import lombok.Data;

import java.util.List;

@Data
public class UserCouponVO {

    private List<UserCouponDO> records;
    private List<TabStat> topTabStat;
    private List<TabStat> cateStat;

    @Data
    public static class TabStat{
        private String name;
        private Integer count;
    }
}
