package co.yixiang.yshop.module.infra.controller.admin.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import co.yixiang.yshop.module.infra.controller.admin.config.vo.AccessTokenDTO;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class WxMiniUtil {

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }
    @Value("${wx.miniapp.app-id}")
    private String appId;

    @Value("${wx.miniapp.app-secret}")
    private String appSecret;

    @Resource(name = "wechatRestTemplate")
    private RestTemplate restTemplate;
    /**
     * 获取accessToken
     */
    public AccessTokenDTO getAccessToken() {
        String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                appId, appSecret);
        String result = HttpUtil.get(url);
        return JSON.parseObject(result, AccessTokenDTO.class);
    }

    /**
     * 根据phoneCode 获取用户手机号【核心方法】
     */
    public String getUserPhone(String phoneCode) {
        AccessTokenDTO tokenDto = getAccessToken();
        if(StrUtil.isNotBlank(tokenDto.getErrcode())){
            throw new RuntimeException("获取accessToken失败:" + tokenDto.getErrmsg());
        }
        String accessToken = tokenDto.getAccess_token();
        String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + accessToken;
        String body = "{\"code\":\"" + phoneCode + "\"}";
        String resp = HttpUtil.post(url, body);
        return resp;
    }

    public String getUserOpenidByCode(String jsCode) {

//        ClientHttpRequestFactory factory = restTemplate.getInterceptors().isEmpty() ? restTemplate.getRequestFactory() : null;
//        log.info("restTemplate底层工厂类：{}", restTemplate.getRequestFactory().getClass().getName());
//        String url = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
//                appId,appSecret,jsCode);
//        try {
//            return restTemplate.getForObject(url, String.class);
//        } catch (RestClientException e) {
//            log.error("获取openid请求异常，jsCode:{}", jsCode, e);
//            return null;
//        }

        String url = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId,appSecret,jsCode);
        log.info("===restTemplate底层工厂类:{}",restTemplate.getRequestFactory().getClass().getName());

        try {
            return restTemplate.getForObject(url, String.class);
        }catch (Exception e){
            log.error("获取openid请求异常，jsCode:{}",jsCode,e);
            return null;
        }
    }

    /**
     * 根据jsCode获取openid + sessionKey（如需）
     */
    public String getOpenId(String jsCode){
        String url = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId,appSecret,jsCode);
        String res = HttpUtil.get(url);
        // 自行解析openid
        return res;
    }

    /**
     * 生成小程序无限码 getwxacodeunlimit
     * @param scene 自定义参数，例 shop_1001，最大32字符，仅字母数字下划线
     * @param page 跳转页面，如 pages/index/index
     * @return png图片字节数组
     */
    public byte[] createUnlimitedQrCode(String scene, String page) {
        AccessTokenDTO tokenDto = getAccessToken();
        if(StrUtil.isNotBlank(tokenDto.getErrcode())){
            throw new RuntimeException("获取accessToken失败:" + tokenDto.getErrmsg());
        }
        String accessToken = tokenDto.getAccess_token();

        String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("scene", scene);
        paramMap.put("page", page);
        paramMap.put("width", 430);
        // 开发阶段：页面未发布也可以生成；上线正式环境建议删除该参数
        paramMap.put("check_path", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(paramMap), headers);

        // 请求，接收byte[]
        byte[] bodyBytes;
        try {
            bodyBytes = restTemplate.postForObject(url, httpEntity, byte[].class);
        }catch (Exception e){
            log.error("生成小程序码调用微信接口异常",e);
            throw new RuntimeException("调用微信小程序码接口异常");
        }

        if(bodyBytes == null){
            throw new RuntimeException("微信返回空数据");
        }
        // 微信出错时返回json字符串，字节很短；png图片字节很大
        if(bodyBytes.length < 200){
            String errJson = new String(bodyBytes);
            log.error("生成小程序码失败，微信返回:{}", errJson);
            throw new RuntimeException("生成小程序码失败:" + errJson);
        }
        return bodyBytes;
    }
}
