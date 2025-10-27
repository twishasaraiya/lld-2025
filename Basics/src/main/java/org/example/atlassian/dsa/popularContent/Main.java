package org.example.atlassian.dsa.popularContent;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        ContentRanking contentRanking = new ContentRanking();

        contentRanking.increasePopularity(1);
        contentRanking.increasePopularity(2);
        System.out.println(contentRanking.getMostPopularContent());

        contentRanking.increasePopularity(1);
        System.out.println(contentRanking.getMostPopularContent());

        contentRanking.decreasePopularity(1);
        contentRanking.decreasePopularity(1);
        System.out.println(contentRanking.getMostPopularContent());

    }
}


class ContentRanking{
    private Map<Integer,Integer> contentPopularityMap;
    private TreeMap<Integer, Set<Integer>> popularContentIdMap;

    public ContentRanking() {
        this.contentPopularityMap = new HashMap<>();
        this.popularContentIdMap = new TreeMap<>();
    }

    public void increasePopularity(int contentId) {
        int oldPop = contentPopularityMap.getOrDefault(contentId,0);
        int newPop = oldPop + 1;
        if(oldPop > 0){
            popularContentIdMap.get(oldPop).remove(contentId);
        }

       contentPopularityMap.put(contentId, newPop);
        popularContentIdMap.computeIfAbsent(newPop , k -> new HashSet<>()).add(contentId);
    }

    public void decreasePopularity(int contentId) {
        int oldPop = contentPopularityMap.getOrDefault(contentId,0);
        int newPop = oldPop - 1;

        popularContentIdMap.get(oldPop).remove(contentId);
        if(popularContentIdMap.get(oldPop).isEmpty()){
            popularContentIdMap.remove(oldPop);
        }

        if(newPop > 0){
            popularContentIdMap.computeIfAbsent(newPop , k -> new HashSet<>()).add(contentId);
            contentPopularityMap.put(contentId, newPop);
        }
    }

    public int getMostPopularContent() {
        return popularContentIdMap.lastKey() != null ? popularContentIdMap.lastEntry().getValue().iterator().next() : -1;
    }
}

enum ContentType{
    VIDEO, PAGE, POST
}

