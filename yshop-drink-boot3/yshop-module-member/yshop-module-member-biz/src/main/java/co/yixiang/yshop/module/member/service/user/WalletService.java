package co.yixiang.yshop.module.member.service.user;

import co.yixiang.yshop.module.member.controller.app.user.vo.UserWalletVO;

import java.util.Map;

public interface WalletService {

    public UserWalletVO getWalletInfo();

    public Map<String,Object> getWalletLog(Integer queryType);

    public String exchangeCode(String code);

}
