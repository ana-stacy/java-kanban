package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import manager.TaskManager;

import typeadapter.DurationTypeAdapter;
import typeadapter.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected TaskManager taskManager;
    protected Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String requestPath = exchange.getRequestURI().getPath();
            String[] pathParts = requestPath.split("/");
            String method = exchange.getRequestMethod();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder
                    .serializeNulls()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                    .create();

            switch(method) {
                case "GET":
                    handleGetMethod(exchange, pathParts);
                    break;
                case "POST":
                    handlePostMethod(exchange);
                    break;
                case "DELETE":
                    handleDeleteMethod(exchange, pathParts);
                    break;
                default:
                    sendUnknownMethod(exchange, "Метод отсутствует");
                    break;
            }
        }
    }

    protected abstract void handleGetMethod(HttpExchange exchange, String[] pathParts) throws IOException;
    protected abstract void handlePostMethod(HttpExchange exchange) throws IOException;
    protected abstract void handleDeleteMethod(HttpExchange exchange, String[] pathParts) throws IOException;

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.close();
    }

    protected void sendTextModification(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(201, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.close();
    }

    protected void sendNotFound(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(404, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.close();
    }

    protected void sendHasInteractions(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(406, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.close();
    }

    protected void sendUnknownMethod(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(500, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.close();
    }
}

