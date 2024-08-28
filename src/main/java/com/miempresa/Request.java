package com.miempresa;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private final Map<String, String> queryParams;

    public Request(String queryString) {
        queryParams = new HashMap<>();
        if (queryString != null && !queryString.isEmpty()) {
            String[] params = queryString.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    queryParams.put(keyValue[0], keyValue[1]);
                }
            }
        }
    }

    public String getValue(String key) {
        return queryParams.get(key);
    }
}
