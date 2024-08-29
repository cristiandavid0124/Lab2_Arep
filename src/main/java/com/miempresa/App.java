package com.miempresa;

import java.util.HashMap;
import java.util.Map;

public class App {
    private static Map<String, Service> services = new HashMap<>();
    private static String staticFilesLocation = "";

    public static void main(String[] args) {
        staticfiles("src/resources");
        get("/hello", (req, resp) -> {
            String name = req.getValues("name");
            return name != null && !name.isEmpty() ? "Hello " + name : "Hello World!";
        });
        get("/pi", (req, resp) -> String.valueOf(Math.PI));
        SimpleWebServer.getInstance().Start();
    }
    

    public static void get(String url, Service s) {
        services.put("/App" + url, s);
        System.out.println("Service registered in: /App" + url);
    }

    public static void staticfiles(String location) {
        staticFilesLocation = location;
    }

    public static Map<String, Service> getServices() {
        return services;
    }

    public static String getStaticFilesLocation() {
        return staticFilesLocation;
    }
}