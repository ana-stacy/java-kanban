package server;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleGetMethod(HttpExchange exchange, String[] pathParts) throws IOException {
        try (exchange) {
            List<Task> historyTasksList = taskManager.getHistory();
            if (!historyTasksList.isEmpty()) {
                String historyTasksListJson = gson.toJson(historyTasksList);
                sendText(exchange, historyTasksListJson);
            } else {
                sendNotFound(exchange, "История пустая");
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