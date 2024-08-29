package com.miempresa;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SimpleWebServer {
    private static SimpleWebServer instance;
    private static final int PORT = 8080;

    private SimpleWebServer() {}

    public static SimpleWebServer getInstance() {
        if (instance == null) {
            instance = new SimpleWebServer();
        }
        return instance;
    }

    public void Start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Listening on port " + PORT);
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    processClientRequest(clientSocket);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + PORT);
        }
    }

    private void processClientRequest(Socket clientSocket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        OutputStream writer = clientSocket.getOutputStream();
        String requestLine = reader.readLine();
        if (requestLine != null) {
            String[] requestParts = requestLine.split(" ");
            String requestedPath = requestParts[1];

            if (requestedPath.startsWith("/App/")) {
                handleServiceRequest(requestedPath, writer);
            } else {
                handleStaticFileRequest(requestedPath, writer);
            }
        }
    }

    private void handleServiceRequest(String path, OutputStream output) throws IOException {
        String[] pathComponents = path.split("\\?");
        String servicePath = pathComponents[0];
        String query = pathComponents.length > 1 ? pathComponents[1] : "";

        Service service = App.getServices().get(servicePath);
        if (service != null) {
            Request request = new Request(query);
            Response response = new Response();
            String result = service.getValue(request, response);
            sendResponse(output, "200 OK", "text/plain", result);
        } else {
            sendResponse(output, "404 Not Found", "text/plain", "Service not found");
        }
    }

    private void handleStaticFileRequest(String path, OutputStream output) throws IOException {
        // Si la ruta es "/", redirigir a "/index.html"
        if ("/".equals(path)) {
            path = "/index.html";
        }
    
        String fullPath = App.getStaticFilesLocation() + path;
        File file = new File(fullPath);
        System.out.println("Trying to serve file: " + file.getAbsolutePath()); // Log for debugging
        if (file.exists() && !file.isDirectory()) {
            String contentType = Files.probeContentType(Paths.get(file.getAbsolutePath()));
            byte[] fileContent = Files.readAllBytes(file.toPath());
            sendResponse(output, "200 OK", contentType, new String(fileContent));
        } else {
            System.out.println("File not found: " + file.getAbsolutePath()); // Log for debugging
            sendResponse(output, "404 Not Found", "text/plain", "File not found");
        }
    }

    private void sendResponse(OutputStream output, String status, String contentType, String body) throws IOException {
        PrintWriter writer = new PrintWriter(output, true);
        writer.println("HTTP/1.1 " + status);
        writer.println("Content-Type: " + contentType);
        writer.println();
        writer.println(body);
    }
}
