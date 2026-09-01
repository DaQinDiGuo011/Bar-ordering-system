package co.yixiang.yshop.module.order.dal.dataobject.storeorder;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("yshop_pay_wechat_param")
public class YshopPayWechatDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 业务订单号 */
    private String orderId;

    /** 微信appId */
    private String appId;

    /** 随机字符串nonceStr */
    private String nonceStr;

    /** 微信package参数 关键字，加反引号 */
    @TableField("`package`")
    private String packageVal;

    /** paySign签名 */
    private String paySign;

    /** 签名类型 signType */
    private String signType;

    /** 时间戳timeStamp */
    private String timeStamp;

    // 公共字段
    private String creator;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private String updater;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Boolean deleted;

    private Long tenantId;
}
