package co.yixiang.yshop.module.coupon.service.couponuser;

import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.framework.mybatis.core.query.LambdaQueryWrapperX;
import co.yixiang.yshop.module.coupon.controller.admin.couponuser.vo.CouponUserCreateReqVO;
import co.yixiang.yshop.module.coupon.controller.admin.couponuser.vo.CouponUserExportReqVO;
import co.yixiang.yshop.module.coupon.controller.admin.couponuser.vo.CouponUserPageReqVO;
import co.yixiang.yshop.module.coupon.controller.admin.couponuser.vo.CouponUserUpdateReqVO;
import co.yixiang.yshop.module.coupon.convert.couponuser.CouponUserConvert;
import co.yixiang.yshop.module.coupon.dal.dataobject.couponuser.CouponUserDO;
import co.yixiang.yshop.module.coupon.dal.mysql.couponuser.CouponUserMapper;
import co.yixiang.yshop.module.coupon.enums.CouponStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static co.yixiang.yshop.framework.common.exception.util.ServiceExceptionUtil.exception;
import static co.yixiang.yshop.module.coupon.enums.ErrorCodeConstants.COUPON_USER_NOT_EXISTS;
import static co.yixiang.yshop.module.coupon.enums.ErrorCodeConstants.USER_USE_NOT_DEL;

/**
 * 用户领的优惠券 Service 实现类
 *
 * @author yshop
 */
@Service
@Validated
public class CouponUserServiceImpl implements CouponUserService {

    @Resource
    private CouponUserMapper userMapper;

    @Override
    public Integer createUser(CouponUserCreateReqVO createReqVO) {
        // 插入
        CouponUserDO user = CouponUserConvert.INSTANCE.convert(createReqVO);
        userMapper.insert(user);
        // 返回
        return user.getId();
    }

    @Override
    public void updateUser(CouponUserUpdateReqVO updateReqVO) {
        // 校验存在
        validateUserExists(updateReqVO.getId());
        // 更新
        CouponUserDO updateObj = CouponUserConvert.INSTANCE.convert(updateReqVO);
        userMapper.updateById(updateObj);
    }

    @Override
    public void deleteUser(Integer id) {
        // 校验存在
        validateUserExists(id);
        // 删除
        userMapper.deleteById(id);
    }

    private void validateUserExists(Integer id) {
        CouponUserDO userDO = userMapper.selectById(id);
        if (userDO == null) {
            throw exception(COUPON_USER_NOT_EXISTS);
        }
        if(userDO.getStatus() == 1){
            throw exception(USER_USE_NOT_DEL);
        }
    }

    @Override
    public CouponUserDO getUser(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<CouponUserDO> getUserList(Integer id) {
        CouponUserExportReqVO exportReqVO = new CouponUserExportReqVO();
        exportReqVO.setCouponId(id);
        return userMapper.selectList(exportReqVO);
    }

    @Override
    public PageResult<CouponUserDO> getUserPage(CouponUserPageReqVO pageReqVO) {
        return userMapper.selectPage(pageReqVO);
    }

    @Override
    public List<CouponUserDO> getUserList(CouponUserExportReqVO exportReqVO) {
        return userMapper.selectList(exportReqVO);
    }

    @Override
    public Integer getUserusableCouponNum(Long userId) {
        LambdaQueryWrapperX<CouponUserDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(CouponUserDO::getUserId, userId);
        wrapper.eq(CouponUserDO::getStatus, CouponStatusEnum.STATUS_0.getValue());
        Long numb = userMapper.selectCount(wrapper);
        return Math.toIntExact(numb);
    }

}
