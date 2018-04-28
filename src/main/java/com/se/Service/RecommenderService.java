package com.se.Service;

import com.se.mapper.HistoryRepository;
import com.se.model.History;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created By IACJ on 2018/4/28
 *
 * 参考博客：https://blog.csdn.net/yeruby/article/details/44154009
 */
@Service
public class RecommenderService {

    @Autowired
    HistoryRepository historyRepository;


    /**
     * 求用户最爱的k个频道
     *
     * @param userId 用户的id
     * @return pList
     * @throws Exception
     */
    public List<Map.Entry<Integer,Double>> recommendTopK(int userId, int k) throws Exception {
        List<History> histories = historyRepository.findAll();
        if(histories==null) {
            throw new Exception("没有历史记录");
        }
        Map<Integer,Map<Integer,Integer>> ucmMap = produceUserChannelMarkMap(histories);

        if (!ucmMap.containsKey(userId) ){
            throw new Exception("用户没有观看记录，无法推荐");
        }

        Map<Integer,Map<Integer,Double>> wMap = produceWMap(ucmMap);
        List<Map.Entry<Integer,Double>> pList = producePList(userId,ucmMap,wMap);
        System.out.println("推荐排名:"+pList);

        if(pList.size() == 0) {
            throw new Exception("推荐失败");
        }
        pList = pList.subList(0, Math.min(k,pList.size()));
        return pList;
    }


    /**
     * 向用户推荐一个频道的ChannelId
     *
     * @param userId 用户的id
     * @return ChannelId 推荐的ChannelId
     * @throws Exception
     */
    public int recommendOne(int userId) throws Exception {
        int rcid = recommendTopK(userId,1).get(0).getKey();
        return rcid;
    }

    /**
     * 产生 UserChannelMap，形式为 Map<userId, Map<ChannelId, Mark>>
     *
     * @param histories 用户观看记录
     * @return UserChannelMap
     */
    private Map<Integer,Map<Integer,Integer>> produceUserChannelMarkMap(List<History> histories){
        Map<Integer,Map<Integer,Integer>> ucmMap = new HashMap<Integer, Map<Integer, Integer>>();
        for (History history : histories){
            int uid = history.getPk().getUserId();
            int cid = history.getPk().getChannelId();
            long lastTime = history.getLastTime();
            int mark = calMark(lastTime);

            if (!ucmMap.containsKey(uid)){
                ucmMap.put(uid,new HashMap<Integer,Integer>());
            }
            ucmMap.get(uid).put(cid,mark);
        }
        return ucmMap;
    }

    /**
     * 产生 W 代表两个频道的相似度，形式为 Map<cid1, Map<cid2, score>>，其中score为cid1与cid2的相似度。
     * 先根据 ucmMap 计算出 N 和 C， 然后根据 N 和 C 计算出 W
     *
     * @param ucmMap
     * @return
     */
    private Map produceWMap(final Map<Integer,Map<Integer,Integer>> ucmMap) {

        // N 形式为 Map<cid, n>，其中n为cid出现过的次数
        Map<Integer,Integer> N = new HashMap<Integer, Integer>();

        // C 形式为 Map<cid1, Map<cid2, n>>，其中n为cid1和cid2在同一用户中出现过的次数
        Map<Integer,Map<Integer,Integer>> C = new HashMap<Integer, Map<Integer, Integer>>();

        // W 形式为 Map<cid1, Map<cid2, score>>，其中score为cid1与cid2的相似度
        Map<Integer,Map<Integer,Double>> W = new HashMap<Integer, Map<Integer, Double>>();


        // 根据 ucmMap 产生 N 和 C
        for(int uid : ucmMap.keySet()) {
            final Map<Integer,Integer> cmMap = ucmMap.get(uid);

            for(int cid1 : cmMap.keySet()) {
                if(!N.containsKey(cid1)) {
                    N.put(cid1,0);
                }
                N.put(cid1,N.get(cid1)+1);

                if(!C.containsKey(cid1)) {
                    C.put(cid1,new HashMap<Integer,Integer>());
                }
                Map<Integer,Integer> cnMap = C.get(cid1);
                for(int cid2 : cmMap.keySet()) {
                    if(cid1 == cid2) {
                        continue;
                    }
                    if(!cnMap.containsKey(cid2)) {
                        cnMap.put(cid2,0);
                    }
                    cnMap.put(cid2,cnMap.get(cid2)+1);
                }
            }
        }

        // 根据 N 和 C 产生 W
        for(int i:C.keySet()) {
            W.put(i,new HashMap<Integer,Double>());
            Map<Integer,Integer> cnMap = C.get(i);
            Map<Integer,Double> wsMap = W.get(i);
            for(int j:cnMap.keySet()) {
                int n = cnMap.get(j);
                wsMap.put(j,1.0*n/Math.sqrt(N.get(i)*N.get(j)));
            }
        }
        return W;
    }


    /**
     * 产生 pList，用户最喜爱的频道列表，形式为 List<Map.Entry<cid,mark>>，其中mark是对用户喜爱度的估计
     *
     * @param userId 用户id
     * @param ucmMap
     * @param W
     * @return pList
     */
    private List<Map.Entry<Integer,Double>> producePList(int userId,
                                                         final Map<Integer, Map<Integer,Integer>> ucmMap,
                                                         final Map<Integer,Map<Integer,Double>> W) {
        Map<Integer,Integer> cmMap = ucmMap.get(userId);
        Map<Integer,Double> pMap = new HashMap<Integer, Double>();
        final int K = 5;
        for(int cid:cmMap.keySet()) {

            List<Map.Entry<Integer,Double>> list = getSortedEntries( W.get(cid));
            for(int k=0; k<K; k++) {
                int j = list.get(k).getKey();
                double w = list.get(k).getValue();

                if(!pMap.containsKey(j)) {
                    pMap.put(j,0.0);
                }
                pMap.put(j,pMap.get(j)+w*cmMap.get(cid));
            }
        }
        List<Map.Entry<Integer,Double>> pList = getSortedEntries(pMap);
        return pList;
    }


    /**
     * 根据用户观看频道的时间，计算用户对频道的喜爱度
     *
     * @param time 观看时间
     * @return mark 喜爱度
     */
    private int calMark(long time) {
        time /= 1000;
        if(time >= 100) {
            return 10;
        }
        return (int)time/10;
    }

    /**
     * 根据 Map 产生一个有序列表
     * 比较依据：value 降序
     *
     * @param integerDoubleMap
     * @return 降序list
     */
    private List<Map.Entry<Integer,Double>> getSortedEntries(Map<Integer,Double> integerDoubleMap) {
        Set<Map.Entry<Integer,Double>> set = integerDoubleMap.entrySet();
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
