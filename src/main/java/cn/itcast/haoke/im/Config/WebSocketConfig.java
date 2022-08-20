package cn.itcast.haoke.im.Config;

import cn.itcast.haoke.im.Interceptor.MessageHandShankInterceptor;
import cn.itcast.haoke.im.handler.MessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private MessageHandShankInterceptor messageHandShankInterceptor;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(this.messageHandler, "/ws/{uid}")
                .setAllowedOrigins("*")
                .addInterceptors(this.messageHandShankInterceptor);
    }
}
