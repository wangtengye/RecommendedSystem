package com.se;

import com.se.mapper.ChannelRepository;
import com.se.mapper.HistoryRepository;
import com.se.mapper.UserRepository;
import com.se.model.Channel;
import com.se.model.History;
import com.se.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by takahiro on 2017/7/2.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application implements EmbeddedServletContainerCustomizer {
    @Autowired
    UserRepository userRepository;
    @Autowired
    HistoryRepository historyRepository;
    @Autowired
    ChannelRepository channelRepository;
    public static void main(String[]args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        container.setPort(8088);
    }
}
