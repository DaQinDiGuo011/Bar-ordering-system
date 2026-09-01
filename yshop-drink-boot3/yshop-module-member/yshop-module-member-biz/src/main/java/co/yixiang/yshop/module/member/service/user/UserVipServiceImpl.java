package co.yixiang.yshop.module.member.service.user;

import co.yixiang.yshop.module.member.controller.app.user.vo.AppOpenVipVO;
import co.yixiang.yshop.module.member.controller.app.user.vo.AppUserVipVO;
import co.yixiang.yshop.module.member.controller.app.user.vo.VipLevelConfigVO;
import co.yixiang.yshop.module.member.dal.dataobject.user.UserVipDO;
import co.yixiang.yshop.module.member.dal.dataobject.user.VipLevelConfigDO;
import co.yixiang.yshop.module.member.dal.mysql.user.UserVipMapper;
import co.yixiang.yshop.module.member.dal.mysql.user.VipLevelConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static co.yixiang.yshop.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Service
public class UserVipServiceImpl implements UserVipService{

    @Autowired
    private UserVipMapper userVipMapper;
    @Autowired
    private VipLevelConfigMapper levelMapper;

    @Override
    public AppUserVipVO getVipInfo() {
        UserVipDO userVip = userVipMapper.selectByUserId(getLoginUserId());
        List<VipLevelConfigDO> levelList = levelMapper.selectAllLevel();
        AppUserVipVO vo = new AppUserVipVO();
        List<VipLevelConfigVO> configVOList = levelList.stream().map(info -> {
            VipLevelConfigVO configVO = new VipLevelConfigVO();
            configVO.setId(info.getId());
            configVO.setLevel(info.getLevel());
            configVO.setColor(info.getColor());
            configVO.setCreateTime(info.getCreateTime());
            configVO.setNeedGrowth(info.getNeedGrowth());
            configVO.setLevelName(info.getLevelName());
            return configVO;
        }).collect(Collectors.toList());
        vo.setVipList(configVOList);
        if(userVip == null){
            vo.setGrowthValue(0L);
        }else{
            vo.setGrowthValue(userVip.getGrowthValue());
        }
        return vo;
    }

    @Override
    public void openVipCard(AppOpenVipVO dto) {
        Long userId = getLoginUserId();
        UserVipDO exist = userVipMapper.selectByUserId(userId);
        if(exist != null){
            throw new RuntimeException("已经开通会员卡");
        }
        UserVipDO entity = new UserVipDO();
        entity.setUserId(userId);
        entity.setVipLevel(1);
        entity.setGrowthValue(0L);
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setGender(dto.getGender());
        entity.setBirthday(dto.getBirthday());
        entity.setOpenTime(LocalDateTime.now());
        userVipMapper.insert(entity);
    }
}
