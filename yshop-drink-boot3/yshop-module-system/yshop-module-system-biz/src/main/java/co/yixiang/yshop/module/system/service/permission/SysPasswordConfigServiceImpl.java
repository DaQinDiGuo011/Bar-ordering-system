package co.yixiang.yshop.module.system.service.permission;

import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.system.controller.admin.permission.vo.permission.SysPasswordConfigPageVO;
import co.yixiang.yshop.module.system.controller.admin.permission.vo.permission.SysPasswordConfigVO;
import co.yixiang.yshop.module.system.convert.auth.SysPasswordConfigConvert;
import co.yixiang.yshop.module.system.dal.dataobject.permission.SysPasswordConfigDO;
import co.yixiang.yshop.module.system.dal.mysql.permission.SysPasswordConfigMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static co.yixiang.yshop.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

@Service
public class SysPasswordConfigServiceImpl extends ServiceImpl<SysPasswordConfigMapper, SysPasswordConfigDO> implements SysPasswordConfigService{

    @Resource
    private SysPasswordConfigMapper sysPasswordConfigMapper;

    @Override
    public SysPasswordConfigVO getByType(String passwordType) {
        LambdaQueryWrapper<SysPasswordConfigDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPasswordConfigDO::getPasswordType, passwordType);
        wrapper.eq(SysPasswordConfigDO::getEnabled,1);
        SysPasswordConfigDO passwordConfigDO = getOne(wrapper);
        return SysPasswordConfigConvert.INSTANCE.convertResp(passwordConfigDO);
    }

    @Override
    public List<SysPasswordConfigVO> getValidInfoList() {
        //查询启用的数据
        List<SysPasswordConfigDO> configDOList = sysPasswordConfigMapper.selectList("enabled", 1);
        return SysPasswordConfigConvert.INSTANCE.converList(configDOList);
    }

    @Override
    public void addInfo(SysPasswordConfigVO configVO) {
        SysPasswordConfigDO configDO = SysPasswordConfigConvert.INSTANCE.convertReq(configVO);
        configDO.setCreateTime(LocalDateTime.now());
        configDO.setUpdateTime(LocalDateTime.now());
        configDO.setCreator(String.valueOf(getLoginUserId()));
        configDO.setUpdater(String.valueOf(getLoginUserId()));
        this.save(configDO);
    }

    @Override
    public void updInfo(SysPasswordConfigVO configVO) {
        SysPasswordConfigDO configDO = SysPasswordConfigConvert.INSTANCE.convertReq(configVO);
        configDO.setUpdateTime(LocalDateTime.now());
        configDO.setUpdater(String.valueOf(getLoginUserId()));
        sysPasswordConfigMapper.updateById(configDO);
    }

    @Override
    public PageResult<SysPasswordConfigVO> selectPage(SysPasswordConfigPageVO reqVO) {
        PageResult<SysPasswordConfigDO> passwordConfigDOPageResult = sysPasswordConfigMapper.selectPage(reqVO);

        return SysPasswordConfigConvert.INSTANCE.convertPage(passwordConfigDOPageResult);
    }
}
