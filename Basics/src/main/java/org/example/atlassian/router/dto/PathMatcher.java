package org.example.atlassian.router.dto;

import java.util.*;

public class PathMatcher {
    private PathTrieNode root;
    private static final String PARAM_KEY = "paramKey";

    public PathMatcher() {
        this.root = new PathTrieNode("HEAD");
    }

    public void addPath(String path, String result) {
        String[] parts = path.trim().split("/");
        if (parts.length == 0) return;
        PathTrieNode ptr = root;
        for (String part : parts) {
            if (!ptr.getChildren().containsKey(part)) {
                PathTrieNode node = new PathTrieNode(part);
                if (part.equals("*")) {
                    node.setName("*");
                    node.setWildCard(true);
                } else if (part.startsWith(":")) {
                    node.setName(PARAM_KEY);
                    node.setParamKey(part.substring(1));
                }else{
                    node.setName(part);
                }
                ptr.getChildren().put(node.getName(), node);
            }
            if(part.startsWith(":")){
                part = PARAM_KEY;
            }
            ptr = ptr.getChildren().get(part);
        }
        ptr.setResult(result);
    }

    public Optional<RouteMatchResult> search(String path) {
        String[] parts = path.trim().split("/");
        Deque<PathMatcher.SearchHelper> dq = new ArrayDeque<>();
        dq.add(new PathMatcher.SearchHelper(root, 0));
        while (!dq.isEmpty()) {
            PathMatcher.SearchHelper curr = dq.pollFirst();
            if (curr.idx == parts.length) {
                if (curr.node != null) {
                    // found match
                    return Optional.of(new RouteMatchResult(curr.node.getResult(), curr.params));
                }
                continue; // check next node since all parts are processed
            }
            String part = parts[curr.idx];
            if (curr.node.getChildren().containsKey(part)) {
                dq.addLast(new PathMatcher.SearchHelper(curr.node.getChildren().get(part), curr.idx + 1));
            }
            if (curr.node.getChildren().containsKey(PARAM_KEY)) {
                Map<String, String> params = new HashMap<>(curr.params);
                params.put(curr.node.getChildren().get(PARAM_KEY).getParamKey(), part);
                dq.addLast(new PathMatcher.SearchHelper(curr.node.getChildren().get(PARAM_KEY), curr.idx + 1, params));
            }
            if (curr.node.getChildren().containsKey("*")) {
                dq.addLast(new PathMatcher.SearchHelper(curr.node.getChildren().get("*"), curr.idx + 1));
            }
        }
        return Optional.empty();
    }

    class SearchHelper {
        PathTrieNode node;
        int idx;
        Map<String, String> params;

        public SearchHelper(PathTrieNode node, int idx) {
            this.node = node;
            this.idx = idx;
            this.params = new HashMap<>();
        }

        public SearchHelper(PathTrieNode node, int idx, Map<String, String> params) {
            this.node = node;
            this.idx = idx;
            this.params = params;
        }
    }
}
