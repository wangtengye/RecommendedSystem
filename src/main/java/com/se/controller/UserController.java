package com.se.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.Service.RecommenderService;
import com.se.jsonmodel.HistoryReturn;
import com.se.jsonmodel.Message;
import com.se.mapper.ChannelRepository;
import com.se.mapper.HistoryRepository;
import com.se.mapper.UserRepository;
import com.se.model.Channel;
import com.se.model.History;
import com.se.model.UserChannelPK;
import com.se.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by takahiro on 2017/7/2.
 */
@Controller
public class UserController {
    @Autowired
    HistoryRepository historyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private RecommenderService recommenderService;

    private ObjectMapper objectMapper = new ObjectMapper();



    @RequestMapping(value = "/home",method = RequestMethod.GET)
    public String home(){
        return "Home";
    }

    @RequestMapping(value = "/getChannelTypes",method = RequestMethod.GET)
    @ResponseBody
    public String getChannelTypes()
            throws JsonProcessingException{
        Message<List<String>> message = new Message<List<String>>();
        List<String> types = channelRepository.findTypes();
        message.setStatus(1);
        message.setData(types);
        return objectMapper.writeValueAsString(message);
    }

    @RequestMapping(value = "/getChannels/{type}",method = RequestMethod.GET)
    @ResponseBody
    public String getChannels(@PathVariable String type)
            throws JsonProcessingException{
        Message<List<Channel>> message = new Message<List<Channel>>();
        List<Channel> channels = channelRepository.findByType(type);
        if(channels==null) {
            message.setError("No channel for such type");
            return objectMapper.writeValueAsString(message);
        }
        message.setStatus(1);
        message.setData(channels);
        return objectMapper.writeValueAsString(message);
    }

    @RequestMapping(value = "/getHistory/{userId}",method = RequestMethod.GET)
    @ResponseBody
    public String getHistory(@PathVariable int userId)throws JsonProcessingException{
        Message<List>message = new Message<List>();
        List<History> histories = historyRepository.findByPkUserId(userId);
        List<HistoryReturn> returns = new ArrayList<HistoryReturn>();
        Channel channel;
        for(History history:histories) {
            int cid = history.getPk().getChannelId();
            channel = channelRepository.findById(cid);
            if(channel == null) {
                message.setError("No such Channel ID");
                return objectMapper.writeValueAsString(message);
            }
            HistoryReturn historyReturn = new HistoryReturn();
            historyReturn.setChannelId(cid);
            historyReturn.setChannelName(channel.getChannelName());
            historyReturn.setType(channel.getType());
            historyReturn.setStartTime(DateUtils.longToDate(history.getStartTime()));
            historyReturn.setLastTime(DateUtils.longToTime(history.getLastTime()));
            returns.add(historyReturn);
        }
        if(returns==null) {
            message.setError("No record for such UserID");
            return objectMapper.writeValueAsString(message);
        }
        message.setStatus(1);
        message.setData(returns);
        return objectMapper.writeValueAsString(message);
    }


    @RequestMapping(value = "/click",method = RequestMethod.POST)
    @ResponseBody
    public String click(@RequestBody String body)
            throws JsonProcessingException {
        Message<History> message = new Message<History>();
        UserChannelPK pk;
        try {
            pk = objectMapper.readValue(body,UserChannelPK.class);
            System.out.println("成功解析上传的json");
        } catch (IOException e) {
            System.out.println("不能解析json");
            message.setError("不能解析上传的json");
            return objectMapper.writeValueAsString(message);
        }
        int channelId = pk.getChannelId();
        Channel channel = channelRepository.findById(channelId);
        if(channel==null) {
            message.setError("no such channel ID");
            return objectMapper.writeValueAsString(message);
        }
        channelRepository.click(channelId);
        History history = new History();
        history.setPk(pk);
        history.setStartTime(System.currentTimeMillis());
        historyRepository.save(history);
        message.setStatus(1);
        message.setData(history);
        return objectMapper.writeValueAsString(message);
    }

    //有关闭当然要有开始
    @RequestMapping(value = "/close",method = RequestMethod.POST)
    @ResponseBody
    public String close(@RequestBody String body)
            throws JsonProcessingException{
        Message<History> message = new Message<History>();
        UserChannelPK pk;
        System.out.println(body);
        try {
            pk = objectMapper.readValue(body,UserChannelPK.class);
            System.out.println("成功解析上传的json");
        } catch (IOException e) {
            System.out.println("不能解析json");
            message.setError("不能解析上传的json");
            return objectMapper.writeValueAsString(message);
        }
        History history = historyRepository.findByPk(pk);
        if(history==null) {
            message.setError("no such channel");
            return objectMapper.writeValueAsString(message);
        }
        long startTime = history.getStartTime();
        history.setLastTime(System.currentTimeMillis()-startTime);
        historyRepository.save(history);
        message.setStatus(1);
        message.setData(history);
        return objectMapper.writeValueAsString(message);
    }

    /**
     * “手气不错”，向用户推荐最喜欢的一个频道
     * @param userId
     * @return  Message<Channel> 用户最喜欢的一个频道
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/recommend/{userId}",method = RequestMethod.GET)
    @ResponseBody
    public String recommandGoodLuck(@PathVariable int userId)
            throws JsonProcessingException {
        Message<Channel> message = new Message();
        try{
            int channelID = recommenderService.recommendOne(userId);
            Channel channel = channelRepository.findById(channelID);
            message.setStatus(1);
            message.setData(channel);
        }catch (Exception e){
            e.printStackTrace();
            message.setError(e.getMessage());
        }
        return objectMapper.writeValueAsString(message);
    }

    /**
     * 向用户推荐最喜欢的 k 个频道
     * @param userId
     * @return 用户最喜欢的 k 个频道
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/recommendtopk/{userId}/{k}",method = RequestMethod.GET)
    @ResponseBody
    public String recommandTopK(@PathVariable int userId,@PathVariable int k)
            throws JsonProcessingException {

        Message<List<Map<String,Object>>> message = new Message();
        try{
            List<Map.Entry<Integer,Double>> pList = recommenderService.recommendTopK(userId,k);
            List<Map<String,Object>> dataList = new ArrayList<Map<String, Object>>();

            for (int i=0;i<pList.size();i++){
                int channelId = pList.get(i).getKey();
                double score = pList.get(i).getValue();
                Channel channel = channelRepository.findById(channelId);

                Map<String,Object> temp = new HashMap<String,Object>();
                temp.put("ChannelInfo",channel);
                temp.put("Score",score);
                dataList.add(temp);
            }

            message.setStatus(1);
            message.setData(dataList);
        }catch (Exception e){
            e.printStackTrace();
            message.setError(e.getMessage());
        }
        return objectMapper.writeValueAsString(message);
    }
}
