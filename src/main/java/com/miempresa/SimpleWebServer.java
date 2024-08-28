package com.miempresa;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class SimpleWebServer {

    private static final int PORT = 8080;
    private static String WEB_ROOT = "src/webroot";
    private static final Map<String, Service> services = new HashMap<>();


    public static void main(String[] args) {
        staticfiles("webroot");
        get("/pi", (req, res) -> String.valueOf(Math.PI));
        get("/hello", (req, res) -> "Hello " + req.getValue("name"));
        get("/suma", (req, res) -> {
            String num1 = req.getValue("num1");
            String num2 = req.getValue("num2");

            if (num1 != null && num2 != null) {
                try {
                    int n1 = Integer.parseInt(num1);
                    int n2 = Integer.parseInt(num2);
                    return String.valueOf(n1 + n2);
                } catch (NumberFormatException e) {
                    return "Parámetros inválidos";
                }
            }
            return "Faltan Parámetros";
        });

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor escuchando en el puerto " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void get(String path, Service action) {
        services.put(path, action);
    }

   
    public static void staticfiles(String folder) {
        WEB_ROOT = "target/classes/" + folder;
        Path path = Paths.get(WEB_ROOT);

        // Verifica si el directorio existe, si no, lo crea
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("Directorio creado: " + WEB_ROOT);
            } catch (IOException e) {
                System.err.println("Error al crear el directorio: " + WEB_ROOT);
                e.printStackTrace();
            }
        } else {
            System.out.println("Usando el directorio existente: " + WEB_ROOT);
        }
    }

   
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 OutputStream out = clientSocket.getOutputStream()) {

                String requestLine = in.readLine();
                if (requestLine == null) return;

                String[] tokens = requestLine.split(" ");
                if (tokens.length < 3) return;

                String method = tokens[0];
                String requestedResource = tokens[1];
                String[] parts = requestedResource.split("\\?");
                String basePath = parts[0];

                if (basePath.startsWith("/")) {
                    basePath = basePath.substring(1);
                }

                if (basePath.startsWith("api/")) {
                    if (services.containsKey(basePath)) {
                        Request req = new Request(parts.length > 1 ? parts[1] : "");
                        Response res = new Response(out);
                        res.setCodeResponse("200 OK");
                        String response = services.get(basePath).getValue(req, res);
                        sendResponse(out, response, res);
                    } else {
                        send404(out);
                    }
                } else {
                    serveStaticFile(requestedResource, out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendResponse(OutputStream out, String response, Response res) throws IOException {
            String httpResponse = "HTTP/1.1 " + res.getCodeResponse() + "\r\n" +
                    "Content-Type: " + res.getContentType() + "\r\n" +
                    "Content-Length: " + response.length() + "\r\n" +
                    "\r\n" +
                    response;
            out.write(httpResponse.getBytes());
            out.flush();
        }

        private void serveStaticFile(String resource, OutputStream out) throws IOException {
            Path filePath = Paths.get(WEB_ROOT, resource);
            if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                // Detectar el tipo MIME
                String contentType = Files.probeContentType(filePath);
                byte[] fileContent = Files.readAllBytes(filePath);
                // Crear el encabezado de la respuesta HTTP
                String responseHeader = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + contentType + "\r\n" +
                        "Content-Length: " + fileContent.length + "\r\n" +
                        "\r\n";
                out.write(responseHeader.getBytes());
                out.write(fileContent);
            } else {
                send404(out);
            }
        }

        private void send404(OutputStream out) throws IOException {
            String response = "HTTP/1.1 404 Not Found\r\n" +
                    "Content-Type: application/json\r\n" +
                    "\r\n" +
                    "{\"error\": \"Not Found\"}";
            out.write(response.getBytes());
        }
    }
}
