package com.se.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @RequestMapping(value = "/recommand/{userId}",method = RequestMethod.GET)
    @ResponseBody
    public String recommand(@PathVariable int userId)
            throws JsonProcessingException {
        Message<Channel> message = new Message();
        List<History> histories = historyRepository.findAll();
        if(histories==null) {
            message.setError("no record");
            return objectMapper.writeValueAsString(message);
        }
        Map<Integer,Map> ucMap = produceMap(histories);
        Map<Integer,Map> wMap = train(ucMap);
        int rcid = recommandForUser(userId,ucMap,wMap);
        if(rcid<0) {
            message.setError("recommand fail");
            return objectMapper.writeValueAsString(message);
        }
        Channel channel = channelRepository.findById(rcid);
        message.setStatus(1);
        message.setData(channel);
        return objectMapper.writeValueAsString(message);
    }

    Map produceMap(List<History> histories) {
        Map<Integer,Map> ucMap = new HashMap<Integer, Map>();
        for(History history:histories) {
            int uid = history.getPk().getUserId();
            if(!ucMap.containsKey(uid)) {
                ucMap.put(uid,new HashMap<Integer,Integer>());
            }
            Map<Integer,Integer> userMap = ucMap.get(uid);
            int cid = history.getPk().getChannelId();
            long lastTime = history.getLastTime();
            int mark = calMark(lastTime);
            userMap.put(cid,mark);
        }
        return ucMap;
    }

    int calMark(long time) {
        time /= 1000;
        if(time >= 100) {
            return 10;
        }
        return (int)time/10;
    }

    Map train(Map<Integer,Map> ucMap) {
        Map<Integer,Integer> N = new HashMap<Integer, Integer>();
        Map<Integer,Map> C = new HashMap<Integer, Map>();
        Map<Integer,Map> W = new HashMap<Integer, Map>();
        for(int uid:ucMap.keySet()) {
            Map<Integer,Integer> uMap = ucMap.get(uid);
            for(int p:uMap.keySet()) {
                if(!N.containsKey(p)) {
                    N.put(p,0);
                }
                if(!C.containsKey(p)) {
                    C.put(p,new HashMap<Integer,Integer>());
                }
                if(!W.containsKey(p)) {
                    W.put(p,new HashMap<Integer,Integer>());
                }
                N.put(p,N.get(p)+1);
                Map<Integer,Integer> cMap = C.get(p);
                for(int q:uMap.keySet()) {
                    if(p!=q) {
                        if(!cMap.containsKey(q)) {
                            cMap.put(q,0);
                        }
                        cMap.put(q,cMap.get(q)+1);
                    }
                }
            }
        }
        for(int i:C.keySet()) {
            Map<Integer,Integer> cMap = C.get(i);
            Map<Integer,Double> wMap = W.get(i);
            for(int j:cMap.keySet()) {
                int n = cMap.get(j);
                wMap.put(j,1.0*n/Math.sqrt(N.get(i)*N.get(j)));
            }
        }
        return W;
    }

    int recommandForUser(int userId,Map<Integer,Map> ucMap,Map<Integer,Map> W) {
        Map<Integer,Integer> markMap = ucMap.get(userId);
        Map<Integer,Double> wMap = W.get(userId);
        Map<Integer,Double> rankMap = new HashMap<Integer, Double>();
        int K = 5;
        for(int i:markMap.keySet()) {
            List<Map.Entry<Integer,Double>> list = getTops(wMap);
            for(int k=0;k<K;k++) {
                int j = list.get(k).getKey();
                double w = list.get(k).getValue();
//                if(!markMap.containsKey(j)) {
                    if(!rankMap.containsKey(j)) {
                        rankMap.put(j,0.0);
                    }
                    rankMap.put(j,rankMap.get(j)+w*markMap.get(i));
//                }
            }
        }
        int rcid = -1;
        double maxw = 0;
        for(int i:rankMap.keySet()) {
            if(maxw < rankMap.get(i)) {
                maxw = rankMap.get(i);
                rcid = i;
            }
        }
        return rcid;
    }

    List getTops(Map<Integer,Double> wMap) {
        Set<Map.Entry<Integer,Double>> set = wMap.entrySet();
        List<Map.Entry<Integer,Double>> list = new ArrayList<Map.Entry<Integer, Double>>(set);
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return -o1.getValue().compareTo(o2.getValue());
            }
        });
        return list;
    }
}
