package co.yixiang.yshop.module.coupon.service.coupon;

import cn.hutool.core.util.IdUtil;
import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.coupon.controller.admin.coupon.vo.CouponCreateReqVO;
import co.yixiang.yshop.module.coupon.controller.admin.coupon.vo.CouponExportReqVO;
import co.yixiang.yshop.module.coupon.controller.admin.coupon.vo.CouponPageReqVO;
import co.yixiang.yshop.module.coupon.controller.admin.coupon.vo.CouponUpdateReqVO;
import co.yixiang.yshop.module.coupon.convert.coupon.CouponConvert;
import co.yixiang.yshop.module.coupon.convert.couponuser.CouponUserConvert;
import co.yixiang.yshop.module.coupon.dal.dataobject.coupon.CouponDO;
import co.yixiang.yshop.module.coupon.dal.dataobject.couponuser.CouponUserDO;
import co.yixiang.yshop.module.coupon.dal.mysql.coupon.CouponMapper;
import co.yixiang.yshop.module.coupon.dal.mysql.couponuser.CouponUserMapper;
import co.yixiang.yshop.module.store.dal.dataobject.storeshop.StoreShopDO;
import co.yixiang.yshop.module.store.dal.mysql.storeshop.StoreShopMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static co.yixiang.yshop.framework.common.exception.util.ServiceExceptionUtil.exception;
import static co.yixiang.yshop.module.coupon.enums.ErrorCodeConstants.COUPON_NOT_EXISTS;
import static co.yixiang.yshop.module.coupon.enums.ErrorCodeConstants.USER_NOT_EXISTS;

/**
 * 优惠券 Service 实现类
 *
 * @author yshop
 */
@Service
@Validated
public class CouponServiceImpl implements CouponService {

    @Resource
    private CouponMapper Mapper;
    @Resource
    private StoreShopMapper storeShopMapper;

    @Resource
    private CouponUserMapper couponUserMapper;

    @Override
    public List<Long> create(CouponCreateReqVO createReqVO) {
        List<Long> idList = new ArrayList<>();
        // 插入
        CouponDO couponDO = CouponConvert.INSTANCE.convert(createReqVO);
        StoreShopDO storeShopDO = storeShopMapper.selectById(createReqVO.getShopId());
        String pref = "DW_";
        //一人一券
        if(couponDO.getType() == 4){
            for(int i=0; i< couponDO.getDistribute(); i++){
                String coup = IdUtil.getSnowflake(0, 0).nextIdStr();
                CouponDO addCoup = CouponConvert.INSTANCE.convert(createReqVO);
                addCoup.setShopName(storeShopDO.getName());
                addCoup.setDistribute(1);
                addCoup.setLimit(1);
                addCoup.setExchangeCode(pref + coup);

                Mapper.insert(addCoup);
                idList.add(addCoup.getId());
            }

        }else{
            String coup = IdUtil.getSnowflake(0, 0).nextIdStr();
            couponDO.setShopName(storeShopDO.getName());
            couponDO.setExchangeCode(pref + coup);
            Mapper.insert(couponDO);
            idList.add(couponDO.getId());
        }

        // 返回
        return idList;
    }

    @Override
    public void update(CouponUpdateReqVO updateReqVO) {
        // 校验存在
        validateExists(updateReqVO.getId());
        // 更新
        CouponDO updateObj = CouponConvert.INSTANCE.convert(updateReqVO);
        StoreShopDO storeShopDO = storeShopMapper.selectById(updateReqVO.getShopId());
        updateObj.setShopName(storeShopDO.getName());
        Mapper.updateById(updateObj);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void distributeUser(CouponUpdateReqVO updateReqVO) {
        validateExists(updateReqVO.getId());

        if(updateReqVO.getUserId() == null){
            throw exception(USER_NOT_EXISTS);
        }
        // 更新
        CouponDO updateObj = CouponConvert.INSTANCE.convert(updateReqVO);

        CouponUserDO createReqVO = CouponUserConvert.INSTANCE.convert(updateObj);
        createReqVO.setId(null);
        createReqVO.setCouponId(Math.toIntExact(updateObj.getId()));
        createReqVO.setUserId(Math.toIntExact(updateObj.getUserId()));
        couponUserMapper.insert(createReqVO);

        updateObj.setUserId(updateObj.getUserId());
        updateObj.setUpdateTime(LocalDateTime.now());
        Mapper.updateById(updateObj);
    }

    @Override
    public void delete(Long id) {
        // 校验存在
        validateExists(id);
        // 删除
        Mapper.deleteById(id);
    }

    private void validateExists(Long id) {
        if (Mapper.selectById(id) == null) {
            throw exception(COUPON_NOT_EXISTS);
        }
    }

    @Override
    public CouponDO get(Long id) {
        return Mapper.selectById(id);
    }

    @Override
    public List<CouponDO> getList() {
        return Mapper.selectList(new LambdaQueryWrapper<CouponDO>().eq(CouponDO::getShopId,0));
    }

    @Override
    public PageResult<CouponDO> getPage(CouponPageReqVO pageReqVO) {
        return Mapper.selectPage(pageReqVO);
    }

    @Override
    public List<CouponDO> getList(CouponExportReqVO exportReqVO) {
        return Mapper.selectList(exportReqVO);
    }

}
