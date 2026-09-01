package co.yixiang.yshop.module.member.service.user;

import co.yixiang.yshop.module.member.controller.app.user.vo.UserCouponVO;
import co.yixiang.yshop.module.member.dal.dataobject.user.UserCouponDO;
import co.yixiang.yshop.module.member.dal.mysql.user.UserCouponMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static co.yixiang.yshop.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Slf4j
@Service
public class UserCouponServiceImpl implements UserCouponService{

    @Resource
    private UserCouponMapper userCouponMapper;

    @Override
    public UserCouponVO getUserCouponList( Integer status, Integer type, String keyword) {
        UserCouponVO dto = new UserCouponVO();
        // 查询卡券列表
        List<UserCouponDO> list = userCouponMapper.selectCouponList(getLoginUserId(), status, type, keyword);
        dto.setRecords(list);

        // 组装顶部tab统计【全部、未使用、已使用、已失效】
        List<UserCouponVO.TabStat> topTab = new ArrayList<>();
        // 这里可以sql查询统计，我简化示例，你可以自行写sql统计

        List<Map<String, Object>> countTopTab = userCouponMapper.countTopTab(getLoginUserId());
        log.info("countTopTab=={}",countTopTab);
//        topTab.add(stat("未使用", (Integer) countTopTab.get(0).get("0")));
//        topTab.add(stat("已使用",(Integer) countTopTab.get(1).get("1")));
//        topTab.add(stat("已失效",(Integer) countTopTab.get(2).get("2")));

        topTab.add(stat("未使用",0));
        topTab.add(stat("已使用",0));
        topTab.add(stat("已失效",0));
        dto.setTopTabStat(topTab);

        // 左侧分类统计
        List<UserCouponVO.TabStat> cateTab = new ArrayList<>();
        List<Map<String, Object>> countCateTab = userCouponMapper.countCateTab(getLoginUserId());
        log.info("countCateTab=={}",countCateTab);
//        cateTab.add(stat("全部", (Integer) countCateTab.get(0).get("0")));
//        cateTab.add(stat("外卖", (Integer) countCateTab.get(1).get("1")));
//        cateTab.add(stat("自提", (Integer) countCateTab.get(2).get("2")));
//        cateTab.add(stat("堂食", (Integer) countCateTab.get(3).get("3")));
//        cateTab.add(stat("快餐", (Integer) countCateTab.get(4).get("4")));
//        cateTab.add(stat("快递", (Integer) countCateTab.get(5).get("5")));

        cateTab.add(stat("全部", 0));
        cateTab.add(stat("外卖", 0));
        cateTab.add(stat("自提",0));
        cateTab.add(stat("堂食", 0));
        cateTab.add(stat("快餐", 0));
        cateTab.add(stat("快递", 0));
        dto.setCateStat(cateTab);
        return dto;
    }

    private UserCouponVO.TabStat stat(String name,Integer count){
        UserCouponVO.TabStat tab = new UserCouponVO.TabStat();
        tab.setName(name);
        tab.setCount(count);
        return tab;
    }
}
