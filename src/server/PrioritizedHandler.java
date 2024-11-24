package server;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.ArrayList;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleGetMethod(HttpExchange exchange, String[] pathParts) throws IOException {
        try (exchange) {
            ArrayList<Task> prioritizedTasksList = taskManager.getPrioritizedTasks();
            if (!prioritizedTasksList.isEmpty()) {
                String prioritizedTasksListJson = gson.toJson(prioritizedTasksList);
                sendText(exchange, prioritizedTasksListJson);
            } else {
                sendNotFound(exchange, "Список задач по времени пуст");
            }
        }
    }

    @Override
    public void handlePostMethod(HttpExchange exchange) throws IOException {
        try (exchange) {
            sendUnknownMethod(exchange, "Метод отсутствует");
        }
    }
    public void handleDeleteMethod(HttpExchange exchange, String[] pathParts) throws IOException {
        try (exchange) {
            sendUnknownMethod(exchange, "Метод отсутствует");
        }
    }
}