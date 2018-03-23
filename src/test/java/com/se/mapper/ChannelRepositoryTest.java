package com.se.mapper;

import com.se.Application;
import com.se.model.Channel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ChannelRepositoryTest {

    @Autowired
    ChannelRepository channelRepository;

    @Test
    public void findByType() throws Exception {
        List<Channel> channels= channelRepository.findByType("中央");
        System.out.println(channels.size());
    }

}