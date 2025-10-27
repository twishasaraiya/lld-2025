package org.example.rippling;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  Design a Music Player like Spotify with below methods
 *
 *   int addSong(string songTitle); // add a song to your music player with incremental song ids starting from 1 void
 *
 *   playSong(int songId, int userId); // user plays a song that is present in the music player void
 *
 *   printMostPlayedSongs(); // print song titles in decreasing order of number of unique users' plays
 */
public class MusicPlayer {
}

interface IPlayer{
    int addSong(String title);
    void playSong(int songId, int userId);
    List<String> mostPlayed();
}

class Player implements IPlayer{
    Map<Integer, HashSet<Integer>> idToPlayMap;
    AtomicInteger primaryKey;
    HashSet<String> uniqueSongs;
    Map<String, Integer> titleToIdMap;
    Map<Integer, String> idToTitleMap;
    public Player() {
        this.titleToIdMap = new HashMap<>();
        this.primaryKey = new AtomicInteger(0);
        this.idToPlayMap = new HashMap<>();
        this.idToTitleMap = new HashMap<>();
    }

    @Override
    public int addSong(String title) {
        if(title == null || title.isEmpty()) throw new RuntimeException("Invalid title");
        titleToIdMap.computeIfAbsent(title, k -> primaryKey.incrementAndGet());
        int id =titleToIdMap.get(title);;
        idToTitleMap.put(id, title);
        return id;
    }

    @Override
    public void playSong(int songId, int userId) {
        idToPlayMap.computeIfAbsent(songId, k -> new LinkedHashSet<>()).add(userId); // Linkedhashset - O(1) and hash set for O(logN) write
    }

    @Override
    public List<String> mostPlayed() {
        PriorityQueue<FreqPair> topSongs = new PriorityQueue<>((a,b) -> {
            if(a.uniqueUsers == b.uniqueUsers) return idToTitleMap.get(a.songId).compareTo(idToTitleMap.get(b.songId));
            return b.uniqueUsers - a.uniqueUsers;
        });
        for (Integer songId: idToPlayMap.keySet()){
            topSongs.add(new FreqPair(songId, idToPlayMap.get(songId).size()));
        }

        List<String> topSongTitle = new ArrayList<>();
        while (!topSongs.isEmpty()){
            topSongTitle.add(idToTitleMap.get(topSongs.poll().songId));
        }
        return topSongTitle;
    }

    class FreqPair{
        final Integer songId;
        final Integer uniqueUsers;

        public FreqPair(Integer songId, Integer uniqueUsers) {
            this.songId = songId;
            this.uniqueUsers = uniqueUsers;
        }
    }
}