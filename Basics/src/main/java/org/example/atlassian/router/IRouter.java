package org.example.atlassian.router;

import org.example.atlassian.router.dto.RouteMatchResult;

import java.util.Optional;

public interface IRouter {
    void addRoute(String path, String result);
    Optional<RouteMatchResult> callRoute(String path);
}
