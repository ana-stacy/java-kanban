package server;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleGetMethod(HttpExchange exchange, String[] pathParts) throws IOException {
        try (exchange) {
            if (pathParts.length == 2) {
                ArrayList<Epic> epicsList = taskManager.getListOfEpics();
                if (!epicsList.isEmpty()){
                    String epicsListJson = gson.toJson(epicsList);
                    sendText(exchange, epicsListJson);
                } else {
                    sendNotFound(exchange, "Эпики отсутствуют");
                }
            } else if (pathParts.length == 3 || pathParts.length == 4) {
                try {
                    int idEpic = Integer.parseInt(pathParts[2]);
                    Epic epic = taskManager.getEpic(idEpic);
                    if (pathParts.length == 3) {
                        String epicJson = gson.toJson(epic);
                        sendText(exchange, epicJson);
                    } else {
                        ArrayList<Subtask> subtasksListByEpic = taskManager.getListOfSubtasksByEpic(idEpic);
                        String subtasksListJson = gson.toJson(subtasksListByEpic);
                        sendText(exchange, subtasksListJson);
                    }
                } catch (NotFoundException e) {
                    sendNotFound(exchange, e.getMessage());
                }
            }
        }
    }

    @Override
    public void handlePostMethod(HttpExchange exchange) throws IOException {
        Epic epic;
        try (exchange) {
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
            epic = gson.fromJson(reader, Epic.class);
            try {
                taskManager.createNewEpic(epic);
                sendTextModification(exchange, "Эпик успешно создан");
            } catch(ValidationException e) {
                sendHasInteractions(exchange, e.getMessage());
            }
        }
    }

    @Override
    public void handleDeleteMethod(HttpExchange exchange, String[] pathParts) throws IOException {
        try (exchange) {
            switch(pathParts.length) {
                case 2 :
                    try {
                        taskManager.clearListOfEpics();
                        sendText(exchange, "Все эпики и их подзадачи удалены");
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    }
                    break;
                case 3 :
                    int idEpic = Integer.parseInt(pathParts[2]);
                    try {
                        taskManager.deleteEpicById(idEpic);
                        sendText(exchange, "Эпик и его подзадачи успешно удалены");
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    }
                    break;
            }
        }
    }
}
