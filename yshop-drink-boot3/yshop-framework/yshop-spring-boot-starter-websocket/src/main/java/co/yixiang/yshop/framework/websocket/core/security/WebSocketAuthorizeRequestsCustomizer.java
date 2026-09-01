package co.yixiang.yshop.framework.websocket.core.security;

import co.yixiang.yshop.framework.security.config.AuthorizeRequestsCustomizer;
import co.yixiang.yshop.framework.websocket.config.WebSocketProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * WebSocket 的权限自定义
 *
 * @author yshop
 */
@RequiredArgsConstructor
public class WebSocketAuthorizeRequestsCustomizer extends AuthorizeRequestsCustomizer {

    private final WebSocketProperties webSocketProperties;

    @Override
    public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers(new AntPathRequestMatcher(webSocketProperties.getPath())).permitAll();
    }

}
