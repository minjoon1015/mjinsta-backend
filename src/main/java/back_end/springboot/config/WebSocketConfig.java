package back_end.springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
     private final JwtChannelInterceptor jwtChannelInterceptor;

     @Value("${spring.rabbitmq.host}")
     private String host;
     @Value("${spring.rabbitmq.port.stomp}")
     private Integer port;
     @Value("${spring.rabbitmq.username}")
     private String username;
     @Value("${spring.rabbitmq.password}")
     private String password;
     @Value("${spring.rabbitmq.virtual-host}")
     private String virtualHost;

     @Override
     public void configureMessageBroker(MessageBrokerRegistry registry) {
          // registry.enableSimpleBroker("/topic", "/queue");
          registry.enableStompBrokerRelay("/topic", "/queue")
                    .setRelayHost(host)
                    .setRelayPort(port)
                    .setClientLogin(username)
                    .setClientPasscode(password)
                    .setSystemLogin(username)
                    .setSystemPasscode(password)
                    .setSystemHeartbeatSendInterval(25000) 
                    .setSystemHeartbeatReceiveInterval(25000)
                    .setVirtualHost(virtualHost);
          registry.setApplicationDestinationPrefixes("/app");          
          registry.setUserDestinationPrefix("/user");
     }    

     @Override
     public void registerStompEndpoints(StompEndpointRegistry registry) {
          registry.addEndpoint("/ws")
                    .setAllowedOriginPatterns("*")
                    .withSockJS()
                    .setHeartbeatTime(25000);
     }

     @Override
     public void configureClientInboundChannel(ChannelRegistration registration) {
          registration.interceptors(jwtChannelInterceptor);
     }
}
