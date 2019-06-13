package com.wsmhz.chat.chat.service;

import com.wsmhz.chat.chat.service.netty.WSServer;
import com.wsmhz.common.business.annotation.WsmhzMicroServiceApplication;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created By TangBiJing
 * Description: 
 */
@EnableFeignClients("com.wsmhz")
@ComponentScan("com.wsmhz.**")
@SpringCloudApplication
@WsmhzMicroServiceApplication
public class ChatserviceApplication {

//    @Bean
//    public WebSocketServerHandshakerFactory init(){
//        return new WebSocketServerHandshakerFactory("ws://localhost:8888/ws", null, true,  65536 * 5);
//    }

    public static void main(String[] args) {
        SpringApplication.run(ChatserviceApplication.class, args);

        WSServer.getInstance().start();
    }
}