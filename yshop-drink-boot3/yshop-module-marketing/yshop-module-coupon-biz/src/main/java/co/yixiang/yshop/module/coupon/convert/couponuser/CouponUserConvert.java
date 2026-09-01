package co.yixiang.yshop.module.coupon.convert.couponuser;

import co.yixiang.yshop.framework.common.pojo.PageResult;
import co.yixiang.yshop.module.coupon.controller.admin.couponuser.vo.CouponUserCreateReqVO;
import co.yixiang.yshop.module.coupon.controller.admin.couponuser.vo.CouponUserExcelVO;
import co.yixiang.yshop.module.coupon.controller.admin.couponuser.vo.CouponUserRespVO;
import co.yixiang.yshop.module.coupon.controller.admin.couponuser.vo.CouponUserUpdateReqVO;
import co.yixiang.yshop.module.coupon.controller.app.coupon.vo.AppMyCouponVO;
import co.yixiang.yshop.module.coupon.dal.dataobject.coupon.CouponDO;
import co.yixiang.yshop.module.coupon.dal.dataobject.couponuser.CouponUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户领的优惠券 Convert
 *
 * @author yshop
 */
@Mapper
public interface CouponUserConvert {

    CouponUserConvert INSTANCE = Mappers.getMapper(CouponUserConvert.class);

    CouponUserDO convert(CouponUserCreateReqVO bean);

    CouponUserDO convert(CouponDO bean);

    CouponUserDO convert(CouponUserUpdateReqVO bean);

    CouponUserRespVO convert(CouponUserDO bean);

    List<AppMyCouponVO> convertList03(List<CouponUserDO> list);

    List<CouponUserRespVO> convertList(List<CouponUserDO> list);

    PageResult<CouponUserRespVO> convertPage(PageResult<CouponUserDO> page);

    List<CouponUserExcelVO> convertList02(List<CouponUserDO> list);

}
