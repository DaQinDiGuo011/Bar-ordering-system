package co.yixiang.yshop.module.member.service.user;

import co.yixiang.yshop.module.member.controller.app.user.vo.UserWalletVO;
import co.yixiang.yshop.module.member.dal.dataobject.user.StoreExchangeCodeDO;
import co.yixiang.yshop.module.member.dal.dataobject.user.UserWalletDO;
import co.yixiang.yshop.module.member.dal.dataobject.user.UserWalletLogDO;
import co.yixiang.yshop.module.member.dal.mysql.user.ExchangeCodeMapper;
import co.yixiang.yshop.module.member.dal.mysql.user.UserWalletLogMapper;
import co.yixiang.yshop.module.member.dal.mysql.user.UserWalletMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static co.yixiang.yshop.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Service
@Validated
public class WalletServiceImpl implements WalletService{

    @Resource
    private UserWalletMapper userWalletMapper;
    @Resource
    private UserWalletLogMapper logMapper;
    @Resource
    private ExchangeCodeMapper exchangeCodeMapper;

    @Override
    public UserWalletVO getWalletInfo() {
        UserWalletVO walletVO = new UserWalletVO();
        UserWalletDO wallet = userWalletMapper.selectByUserId(getLoginUserId());
        if(wallet == null){
            walletVO.setBalance(BigDecimal.ZERO);
            walletVO.setTotalRecharge(BigDecimal.ZERO);
            walletVO.setTotalGift(BigDecimal.ZERO);
            walletVO.setGrowValue(0);
        }else{
            walletVO.setBalance(wallet.getBalance());
            walletVO.setTotalRecharge(wallet.getTotalRecharge());
            walletVO.setTotalGift(wallet.getTotalGift());
            walletVO.setGrowValue(wallet.getGrowValue());
        }
        return walletVO;
    }

    @Override
    public Map<String, Object> getWalletLog(Integer queryType) {
        // type=0 支出(type=2)，其他收入(type=1)
        int sqlType = queryType == 0 ? 2 : 1;
        Long userId = getLoginUserId();
        List<UserWalletLogDO> list = logMapper.selectLog(userId, sqlType);

        // 支出总额
        Map<String,BigDecimal> paySumMap = logMapper.sumPayMoney(userId);
        if(paySumMap == null) {
            paySumMap = new HashMap<>();
        }
        BigDecimal payTotal = paySumMap.get("sum") == null ? BigDecimal.ZERO : paySumMap.get("sum");

        UserWalletVO wallet = getWalletInfo();

        Map<String,Object> result = new HashMap<>();
        result.put("list", list);
        result.put("balance", wallet.getBalance());
        result.put("payTotal", payTotal);
        result.put("rechargeTotal", wallet.getTotalRecharge());
        result.put("giftTotal", wallet.getTotalGift());
        return result;
    }

    // 兑换储值码【事务】
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String exchangeCode(String code) {
        StoreExchangeCodeDO exchangeCode = exchangeCodeMapper.selectByCode(code);
        if(exchangeCode == null){
            return "兑换码不存在";
        }
        if(exchangeCode.getStatus() == 1){
            return "兑换码已被使用";
        }
        BigDecimal money = exchangeCode.getMoney();
        Long userId = getLoginUserId();
        // 1.标记兑换码已使用
        exchangeCodeMapper.updateUsed(code, userId);
        // 2.钱包余额增加
        userWalletMapper.addBalance(userId, money);
        // 3.新增流水 类型1收入
        UserWalletLogDO log = new UserWalletLogDO();
        log.setUserId(userId);
        log.setType(1);
        log.setMoney(money);
        log.setRemark("兑换储值码");
        logMapper.insert(log);
        return null; // null代表成功
    }
}
