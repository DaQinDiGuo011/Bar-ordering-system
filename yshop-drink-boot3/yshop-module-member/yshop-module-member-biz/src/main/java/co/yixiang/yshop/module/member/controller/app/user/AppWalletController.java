package co.yixiang.yshop.module.member.controller.app.user;

import co.yixiang.yshop.framework.common.pojo.CommonResult;
import co.yixiang.yshop.module.member.controller.app.user.vo.UserWalletVO;
import co.yixiang.yshop.module.member.service.user.WalletService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wallet")
public class AppWalletController {


    @Resource
    private WalletService walletService;

    /**
     * 1. 获取钱包信息
     */
    @GetMapping("/info")
    public CommonResult<UserWalletVO> getInfo(){
        // ========== 你的鉴权逻辑，从token获取userId（等同于authCheck中间件）
        UserWalletVO wallet = walletService.getWalletInfo();
        return CommonResult.success(wallet);
    }

    /**
     * 2. 余额明细
     */
    @GetMapping("/log")
    public CommonResult<Map<String,Object>> getLog(@RequestParam Integer type){
        Map<String, Object> data = walletService.getWalletLog(type);
        return CommonResult.success(data);
    }

    /**
     * 3. 兑换储值码
     */
    @PostMapping("/exchange")
    public CommonResult<Map<String, Object>> exchange(@RequestBody Map<String,String> body){

        Map<String, Object> result = new HashMap<>();
        String code = body.get("code");
        String errMsg = walletService.exchangeCode(code);
        if(errMsg != null){
            result.put("code", "500");
            result.put("msg", errMsg);
        }else{
            result.put("code", "200");
            result.put("msg", "兑换成功");
        }
        return CommonResult.success(result);
    }

}
