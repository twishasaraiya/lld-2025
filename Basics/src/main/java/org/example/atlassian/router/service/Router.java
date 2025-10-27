package org.example.atlassian.router.service;

import org.example.atlassian.router.IRouter;
import org.example.atlassian.router.dto.PathMatcher;
import org.example.atlassian.router.dto.RouteMatchResult;

import java.util.*;

public class Router implements IRouter {
//    private Map<String, String> pathMap;
    private PathMatcher pathMatcher;

    public Router() {
        this.pathMatcher = new PathMatcher();
    }

    @Override
    public void addRoute(String path, String result) {
        if(path == null || result == null){
            throw new RuntimeException("Input parameters cannot be null");
        }
        if(path.trim().isBlank() || result.trim().isBlank()){
            throw new RuntimeException("Input parameters cannot be empty");
        }
        pathMatcher.addPath(path,result);

    }

    @Override
    public Optional<RouteMatchResult> callRoute(String path) {
        return pathMatcher.search(path);
    }
}

