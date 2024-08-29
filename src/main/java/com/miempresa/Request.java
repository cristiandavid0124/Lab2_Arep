package com.miempresa;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private Map<String, String> queryParams;

    public Request(String queryString) {
        queryParams = QueryString(queryString);
    }

    public String getValues(String key) {
        return queryParams.get(key);
    }

    private Map<String, String> QueryString(String query) {
        Map<String, String> parameters = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] keyValuePairs = query.split("&");
            for (String keyValuePair : keyValuePairs) {
                String[] keyValue = keyValuePair.split("=");
                if (keyValue.length == 2) {
                    parameters.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return parameters;
    }
    
}