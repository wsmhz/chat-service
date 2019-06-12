package com.wsmhz.chat.chat.service;

import org.springframework.boot.SpringApplication;

import com.wsmhz.common.business.annotation.WsmhzMicroServiceApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.client.SpringCloudApplication;
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
    public static void main(String[] args) {
        SpringApplication.run(ChatserviceApplication.class, args);
    }
}