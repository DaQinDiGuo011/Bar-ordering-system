package co.yixiang.yshop.module.coupon.dal.mysql.couponuser;

import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.framework.mybatis.core.mapper.BaseMapperX;
import co.yixiang.yshop.framework.mybatis.core.query.LambdaQueryWrapperX;
import co.yixiang.yshop.module.coupon.controller.admin.couponuser.vo.CouponUserExportReqVO;
import co.yixiang.yshop.module.coupon.controller.admin.couponuser.vo.CouponUserPageReqVO;
import co.yixiang.yshop.module.coupon.dal.dataobject.couponuser.CouponUserDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户领的优惠券 Mapper
 *
 * @author yshop
 */
@Mapper
public interface CouponUserMapper extends BaseMapperX<CouponUserDO> {

    default PageResult<CouponUserDO> selectPage(CouponUserPageReqVO reqVO) {
        LambdaQueryWrapperX<CouponUserDO> wrapper = new LambdaQueryWrapperX();

            if(reqVO.getDeleted() == -1){
            wrapper.in(CouponUserDO::getDeleted, true, false);
        }
        wrapper.eqIfPresent(CouponUserDO::getShopId, reqVO.getShopId())
                .likeIfPresent(CouponUserDO::getShopName, reqVO.getShopName())
                .eqIfPresent(CouponUserDO::getTitle, reqVO.getTitle())
                .eqIfPresent(CouponUserDO::getLeast, reqVO.getLeast())
                .eqIfPresent(CouponUserDO::getValue, reqVO.getValue())
                .eqIfPresent(CouponUserDO::getType, reqVO.getType())
                .eqIfPresent(CouponUserDO::getScore, reqVO.getScore())
                .eqIfPresent(CouponUserDO::getInstructions, reqVO.getInstructions())
                .eqIfPresent(CouponUserDO::getImage, reqVO.getImage())
                .eqIfPresent(CouponUserDO::getUserId, reqVO.getUserId())
                .eqIfPresent(CouponUserDO::getStatus, reqVO.getStatus())
                .eqIfPresent(CouponUserDO::getCouponId, reqVO.getCouponId())
                .eqIfPresent(CouponUserDO::getExchangeCode, reqVO.getExchangeCode())
                .orderByDesc(CouponUserDO::getId);
        return selectPage(reqVO, wrapper);
    }

    default List<CouponUserDO> selectList(CouponUserExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<CouponUserDO>()
                .eqIfPresent(CouponUserDO::getShopId, reqVO.getShopId())
                .likeIfPresent(CouponUserDO::getShopName, reqVO.getShopName())
                .eqIfPresent(CouponUserDO::getTitle, reqVO.getTitle())
                .eqIfPresent(CouponUserDO::getLeast, reqVO.getLeast())
                .eqIfPresent(CouponUserDO::getValue, reqVO.getValue())
                .eqIfPresent(CouponUserDO::getType, reqVO.getType())
                .eqIfPresent(CouponUserDO::getScore, reqVO.getScore())
                .eqIfPresent(CouponUserDO::getInstructions, reqVO.getInstructions())
                .eqIfPresent(CouponUserDO::getImage, reqVO.getImage())
                .eqIfPresent(CouponUserDO::getUserId, reqVO.getUserId())
                .eqIfPresent(CouponUserDO::getStatus, reqVO.getStatus())
                .eqIfPresent(CouponUserDO::getCouponId, reqVO.getCouponId())
                .eqIfPresent(CouponUserDO::getExchangeCode, reqVO.getExchangeCode())
                .orderByDesc(CouponUserDO::getId));
    }

}
