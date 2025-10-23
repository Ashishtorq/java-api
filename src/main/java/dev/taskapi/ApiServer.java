package dev.taskapi;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.taskapi.model.Task;
import dev.taskapi.store.TaskStore;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ApiServer {
    private static final int PORT = 8000;
    private final TaskStore store = new TaskStore();
    private final Gson gson = new Gson();

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Task API server started on http://localhost:" + PORT);
    }

    class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                // path: /tasks or /tasks/{id}
                String[] parts = path.split("/");
                if (parts.length == 2 || (parts.length==3 && parts[2].isEmpty())) {
                    // /tasks
                    if (method.equalsIgnoreCase("GET")) {
                        writeJson(exchange, 200, gson.toJson(store.list()));
                        return;
                    } else if (method.equalsIgnoreCase("POST")) {
                        String body = readBody(exchange);
                        Task t = gson.fromJson(body, Task.class);
                        if (t.getTitle() == null) {
                            writeJson(exchange, 400, "{\"error\":\"title required\"}");
                            return;
                        }
                        Task created = store.create(t);
                        writeJson(exchange, 201, gson.toJson(created));
                        return;
                    }
                } else if (parts.length == 3) {
                    // /tasks/{id}
                    int id = Integer.parseInt(parts[2]);
                    if (method.equalsIgnoreCase("GET")) {
                        Optional<Task> t = store.get(id);
                        if (t.isPresent()) {
                            writeJson(exchange, 200, gson.toJson(t.get()));
                        } else {
                            writeJson(exchange, 404, "{\"error\":\"not found\"}");
                        }
                        return;
                    } else if (method.equalsIgnoreCase("PUT")) {
                        String body = readBody(exchange);
                        Task patch;
                        try {
                            patch = gson.fromJson(body, Task.class);
                        } catch (JsonSyntaxException e) {
                            writeJson(exchange, 400, "{\"error\":\"invalid json\"}");
                            return;
                        }
                        Optional<Task> updated = store.update(id, patch);
                        if (updated.isPresent()) {
                            writeJson(exchange, 200, gson.toJson(updated.get()));
                        } else {
                            writeJson(exchange, 404, "{\"error\":\"not found\"}");
                        }
                        return;
                    }
                }

                writeJson(exchange, 405, "{\"error\":\"method not allowed\"}");
            } catch (NumberFormatException e) {
                writeJson(exchange, 400, "{\"error\":\"invalid id\"}");
            } catch (Exception e) {
                e.printStackTrace();
                writeJson(exchange, 500, "{\"error\":\"internal error\"}");
            }
        }

        private String readBody(HttpExchange exchange) throws IOException {
            try (InputStream is = exchange.getRequestBody()) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        private void writeJson(HttpExchange exchange, int status, String body) throws IOException {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new ApiServer().start();
    }
}
