package com.se.controller;

import com.se.mapper.ChannelRepository;
import com.se.mapper.HistoryRepository;
import com.se.model.Channel;
import com.se.model.History;
import com.se.model.UserChannelPK;
import org.omg.CORBA.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by takahiro on 2017/7/3.
 */
@Controller
public class AddDataController {
    public static String[]channels = new String[]{"cctv1,中央","cctv2,中央","cctv3,中央","cctv4,中央"
    ,"cctv7,中央","cctv8,中央","cctv9,中央","cctv10,中央","cctv11,中央","cctv12,中央","cctv13,中央","cctv14,中央"
            , "广东卫视,地方卫视","湖南卫视,地方卫视","东方卫视,地方卫视","江苏卫视,地方卫视","浙江卫视,地方卫视"
    ,"辽宁卫视,地方卫视","广西卫视,地方卫视","北京卫视,地方卫视","山东卫视,地方卫视","四川卫视,地方卫视","河南卫视,地方卫视"
    ,"深圳卫视,地方卫视","天津卫视,地方卫视","云南卫视,地方卫视"
    ,"广东体育,体育","广州竞赛,体育","cctv5,体育","北京体育,体育","风云足球,体育","欧洲足球,体育","五星体育,体育"
    ,"南方影视,影视","江苏影视,影视","cctv6,影视","南方影视,影视","东方电影,影视"
    ,"翡翠台,港台","华娱卫视,港台","凤凰卫视,港台","星空卫视,港台"};

    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private HistoryRepository historyRepository;

    @RequestMapping(value = "/addChannel",method = RequestMethod.GET)
    @ResponseBody
    public String addChannel(){
        for (int i = 0; i < channels.length; i++) {
            String[]groups = channels[i].split(",");
            String channelName = groups[0];
            String type = groups[1];
            Channel channel = new Channel();
            channel.setType(type);
            channel.setChannelName(channelName);
            channelRepository.save(channel);
        }
        return "添加成功";
    }

    //持续观看时长为随机的，从5s到5h,百分之80概率为一个小时以内，百分之20概率为大于一个小时x
    @RequestMapping(value = "/addHistory",method = RequestMethod.GET)
    @ResponseBody
    public String addHistory()throws ParseException{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Random userRandom = new Random(),channelRandom = new Random();
        Random timeRandom = new Random(),lastTimeRandom = new Random();
        for (int i = 1; i <= 10; i++) {
            int userId = i;
            for(int j=0;j<100;j++) {
                int channelId = channelRandom.nextInt(channels.length/10*i)+1;
                int year = 2014+timeRandom.nextInt(3);
                int month = 1+timeRandom.nextInt(12);
                int day = 1+timeRandom.nextInt(28);
                int hour = timeRandom.nextInt(24);
                int minute = timeRandom.nextInt(60);
                int second = timeRandom.nextInt(60);
                String time = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
                long startTime = format.parse(time).getTime();
                long lastTime = 0;

                int percent = lastTimeRandom.nextInt(10);
                if (percent < 8) {
                    lastTime = lastTimeRandom.nextInt(3600000)+5000;
                } else {
                    lastTime = lastTimeRandom.nextInt(9000000)+3600000;
                }
                History history = new History();
                UserChannelPK pk = new UserChannelPK();
                pk.setUserId(userId);
                pk.setChannelId(channelId);
                history.setStartTime(startTime);
                history.setLastTime(lastTime);
                history.setPk(pk);
                historyRepository.save(history);
            }

        }
        return "添加成功";
    }
}
