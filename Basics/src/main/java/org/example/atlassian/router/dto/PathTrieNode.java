package org.example.atlassian.router.dto;

import java.util.HashMap;
import java.util.Map;

public class PathTrieNode {
    private String name;
    private String result;
    private Map<String, PathTrieNode> children;
    private boolean isWildCard;
    private String paramKey; // if isPathParam == true

    public PathTrieNode(String name) {
        this.name = name;
        this.children = new HashMap<>();
        this.isWildCard = false;
    }

    public String getName() {
        return name;
    }

    public Map<String, PathTrieNode> getChildren() {
        return children;
    }

    public boolean isWildCard() {
        return isWildCard;
    }


    public void setWildCard(boolean wildCard) {
        isWildCard = wildCard;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getResult() {
        return result;
    }
}
