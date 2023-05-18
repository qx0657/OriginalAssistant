package fun.qianxiao.originalassistant.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PostStatisticsResult
 *
 * @Author QianXiao
 * @Date 2023/5/14
 */
public class PostStatisticsResult {
    /**
     * 帖子数
     */
    private int postCount;
    /**
     * 原创数
     */
    private int originalCount;
    /**
     * 收录数
     */
    private int inclusionCount;
    /**
     * 精贴数
     */
    private int goodPostCnt;
    /**
     * 帖子前缀统计数量Map
     */
    private Map<String, Integer> prefixCountMap = new HashMap<>();
    /**
     * 帖子前缀统计数量Map（仅原创板块）
     */
    private Map<String, Integer> prefixCountOriginalMap = new HashMap<>();
    /**
     * 原创帖子葫芦数集合
     */
    private List<Integer> scoreOriginalList;

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getOriginalCount() {
        return originalCount;
    }

    public void setOriginalCount(int originalCount) {
        this.originalCount = originalCount;
    }

    public void addOriginalCnt() {
        this.originalCount++;
    }

    public int getInclusionCount() {
        return inclusionCount;
    }

    public void setInclusionCount(int inclusionCount) {
        this.inclusionCount = inclusionCount;
    }

    public void addInclusionPostCnt() {
        this.inclusionCount++;
    }

    public int getGoodPostCnt() {
        return goodPostCnt;
    }

    public void setGoodPostCnt(int goodPostCnt) {
        this.goodPostCnt = goodPostCnt;
    }

    public void addGoodPostCnt() {
        this.goodPostCnt++;
    }

    public Map<String, Integer> getPrefixCountMap() {
        return prefixCountMap;
    }

    public void setPrefixCountMap(Map<String, Integer> prefixCountMap) {
        this.prefixCountMap = prefixCountMap;
    }

    public void addPrefixCount(String prefix) {
        if (!prefixCountMap.containsKey(prefix)) {
            prefixCountMap.put(prefix, 1);
        } else {
            prefixCountMap.put(prefix, prefixCountMap.get(prefix) + 1);
        }
    }

    public Map<String, Integer> getPrefixCountOriginalMap() {
        return prefixCountOriginalMap;
    }

    public void setPrefixCountOriginalMap(Map<String, Integer> prefixCountOriginalMap) {
        this.prefixCountOriginalMap = prefixCountOriginalMap;
    }

    public void addPrefixOriginalCount(String prefix) {
        if (!prefixCountOriginalMap.containsKey(prefix)) {
            prefixCountOriginalMap.put(prefix, 1);
        } else {
            prefixCountOriginalMap.put(prefix, prefixCountOriginalMap.get(prefix) + 1);
        }
    }

    public List<Integer> getScoreOriginalList() {
        return scoreOriginalList;
    }

    public void setScoreOriginalList(List<Integer> scoreOriginalList) {
        this.scoreOriginalList = scoreOriginalList;
    }

    public void addOriginalPostScore(int score) {
        if (scoreOriginalList == null) {
            if (postCount != 0) {
                scoreOriginalList = new ArrayList<>(postCount);
            } else {
                scoreOriginalList = new ArrayList<>(100);
            }
        }
        scoreOriginalList.add(score);
    }
}
