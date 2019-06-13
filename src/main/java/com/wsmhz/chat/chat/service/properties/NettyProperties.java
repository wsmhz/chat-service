package com.wsmhz.chat.chat.service.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created By TangBiJing On 2019/4/3
 * Description:
 */
@Configuration("NettyProperties")
@ConfigurationProperties(prefix = "wsmhz.netty")
@EnableConfigurationProperties(NettyProperties.class)
@Getter
@Setter
public class NettyProperties {

    private Integer port = 8888;

}
