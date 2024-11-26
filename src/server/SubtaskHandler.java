package server;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import manager.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleGetMethod(HttpExchange exchange, String[] pathParts) throws IOException {
        try (exchange) {
            switch (pathParts.length) {
                case 2:
                    ArrayList<Subtask> subtasksList = taskManager.getListOfSubtasks();
                    if (!subtasksList.isEmpty()) {
                        String subtasksListJson = gson.toJson(subtasksList);
                        sendText(exchange, subtasksListJson);
                    } else {
                        sendNotFound(exchange, "Подзадачи отсутствуют");
                    }
                    break;
                case 3:
                    int idSubtask = Integer.parseInt(pathParts[2]);
                    Subtask subtask = null;
                    try {
                        subtask = taskManager.getSubtask(idSubtask);
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    }
                    String subtaskJson = gson.toJson(subtask);
                    sendText(exchange, subtaskJson);
                    break;
            }
        }
    }

    @Override
    public void handlePostMethod(HttpExchange exchange) throws IOException {
        Subtask subtask;
        try (exchange) {
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
            subtask = gson.fromJson(reader, Subtask.class);
            if (subtask.getId() == null || subtask.getId() == 0) {
                try {
                    taskManager.createNewSubtask(subtask);
                    sendTextModification(exchange, "Подзадача успешно создана");
                } catch (ValidationException e) {
                    sendHasInteractions(exchange, e.getMessage());
                }
            } else {
                try {
                    taskManager.updateSubtask(subtask);
                    sendTextModification(exchange, "Подзадача успешно обновлена");
                } catch (ValidationException e) {
                    sendHasInteractions(exchange, e.getMessage());
                } catch (NotFoundException e) {
                    sendNotFound(exchange, e.getMessage());
                }
            }
        }
    }

    @Override
    public void handleDeleteMethod(HttpExchange exchange, String[] pathParts) throws IOException {
        try (exchange) {
            switch (pathParts.length) {
                case 2:
                    try {
                        taskManager.clearListOfSubtasks();
                        sendText(exchange, "Все подзадачи удалены");
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    }
                    break;
                case 3:
                    int idSubtask = Integer.parseInt(pathParts[2]);
                    try {
                        taskManager.deleteSubtaskById(idSubtask);
                        sendText(exchange, "Задача успешно удалена");
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    }
                    break;
            }
        }
    }
}
