package co.yixiang.yshop.module.store.controller.admin.storeshop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static co.yixiang.yshop.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
* 门店管理 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class StoreShopBaseVO {

    @Schema(description = "店铺名称", required = true, example = "李四")
    @NotNull(message = "店铺名称不能为空")
    private String name;

    @Schema(description = "店铺电话", required = true)
    @NotNull(message = "店铺电话不能为空")
    private String mobile;

    @Schema(description = "图片", required = true)
    @NotNull(message = "图片不能为空")
    private String image;

    @Schema(description = "多张图片", required = true)
    @NotNull(message = "多张图片不能为空")
    private List<String> images;

    @Schema(description = "详细地址", required = true)
    @NotNull(message = "详细地址不能为空")
    private String address;

    @Schema(description = "地图定位地址", required = true)
//    @NotNull(message = "地图定位地址不能为空")
    private String addressMap;

    @Schema(description = "经度", required = true)
//    @NotNull(message = "经度不能为空")
    private String lng;

    @Schema(description = "纬度", required = true)
//    @NotNull(message = "纬度不能为空")
    private String lat;

    @Schema(description = "外卖配送距离,单位为千米。0表示不送外卖")
//    @NotNull(message = "外卖配送距离,单位为千米。0表示不送外卖不能为空")
    private Integer distance;

    @Schema(description = "起送价钱", example = "15157")
    private BigDecimal minPrice;

    @Schema(description = "配送价格", example = "11771")
    private BigDecimal deliveryPrice;

    @Schema(description = "公告")
//    @NotNull(message = "公告不能为空")
    private String notice;

    @Schema(description = "是否营业:0=否,1=是", required = true, example = "2")
    @NotNull(message = "是否营业:0=否,1=是不能为空")
    private Integer status;

    @Schema(description = "管理员id", required = true, example = "22251")
//    @NotNull(message = "管理员id不能为空")
    private List<String> adminId;

    @Schema(description = "打印机id", required = true, example = "5596")
    //@NotNull(message = "打印机id不能为空")
    private String uniprintId;

    @Schema(description = "营业开始时间", required = true)
//    @NotNull(message = "营业开始时间不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private Date startTime;

    @Schema(description = "营业结束时间", required = true)
//    @NotNull(message = "营业结束时间不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private Date endTime;

    @Schema(description = "余额", required = true, example = "5596")
    private BigDecimal balance;

    @Schema(description = "logo图片")
    private String logoImage;

    @Schema(description = "经营内容")
    private String businessContent;

    @Schema(description = "wifi信息")
    private String wifiInfo;

    @Schema(description = "wifi密码")
    private String wifiPwd;

    @Schema(description = "营业开始时间")
    private String businessStartStr;

    @Schema(description = "营业结束时间")
    private String businessEndStr;

    @Schema(description = "优惠券使用数量限制，0=不限制")
    private Integer couponUseNumLimit;

    @Schema(description = "优惠券使用额度限制(单订单总抵扣上限)，0.00=不限制")
    private BigDecimal couponUseAmountLimit;

    private String clientAdImages;

    private Integer orderUnpayCancelSecond;
}
