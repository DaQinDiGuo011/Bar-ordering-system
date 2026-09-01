package co.yixiang.yshop.module.member.controller.app.user;

import co.yixiang.yshop.framework.common.pojo.CommonResult;
import co.yixiang.yshop.module.member.controller.app.user.vo.StorePointExchangeVO;
import co.yixiang.yshop.module.member.controller.app.user.vo.StorePointGoodsVO;
import co.yixiang.yshop.module.member.controller.app.user.vo.UserPointLogVO;
import co.yixiang.yshop.module.member.service.user.PointService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/point")
public class AppPointController {

    @Resource
    private PointService pointService;

    /** 2.积分明细记录 */
    @GetMapping("/log")
    public CommonResult<List<UserPointLogVO>> getLog(){
        List<UserPointLogVO> list = pointService.getPointLog();
        return CommonResult.success(list);
    }

    /** 3.积分商城商品列表 */
    @GetMapping("/goods")
    public CommonResult<List<StorePointGoodsVO>> getGoods(){
        List<StorePointGoodsVO> list = pointService.getShopGoods();
        return CommonResult.success(list);
    }

    /** 4.兑换记录列表 status:0未完成 1已完成 */
    @GetMapping("/exchange/list")
    public CommonResult<List<StorePointExchangeVO>> exchangeList(@RequestParam(required = false) Integer status){

        List<StorePointExchangeVO> list = pointService.getExchangeList(status);
        return CommonResult.success(list);
    }

    /** 5.积分兑换商品 */
    @PostMapping("/exchange")
    public CommonResult<Map<String, Object>> exchange(@RequestBody Map<String,Integer> body){
        Map<String, Object> result = new HashMap<>();
        Integer goodsId = body.get("goodsId");
        String err = pointService.exchangeGoods(goodsId);
        if(err != null){
            result.put("code", "500");
            result.put("msg", err);
        }else{
            result.put("code","200");
            result.put("msg", "兑换成功");
        }
        return CommonResult.success(result);
    }

}
