package org.example.atlassian.router.dto;

import java.util.Collections;
import java.util.Map;

public class RouteMatchResult{
    private String output;
    private Map<String, String> paramsMap;

    public RouteMatchResult(String output, Map<String, String> paramsMap) {
        this.output = output;
        this.paramsMap = Collections.unmodifiableMap(paramsMap);
    }

    public String getOutput() {
        return output;
    }

    public Map<String, String> getParamsMap() {
        return paramsMap;
    }
}
