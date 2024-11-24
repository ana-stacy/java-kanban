package server;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleGetMethod(HttpExchange exchange, String[] pathParts) throws IOException {
       try (exchange) {
           switch (pathParts.length) {
               case 2:
                   ArrayList<Task> tasksList = taskManager.getListOfTasks();
                   if (!tasksList.isEmpty()) {
                       String tasksListJson = gson.toJson(tasksList);
                       sendText(exchange, tasksListJson);
                   } else {
                       sendNotFound(exchange, "Задачи отсутствуют");
                   }
                   break;
               case 3:
                   int idTask = Integer.parseInt(pathParts[2]);
                   Task task = null;
                   try {
                       task = taskManager.getTask(idTask);
                   } catch (NotFoundException e) {
                       sendNotFound(exchange, e.getMessage());
                   }
                   String taskJson = gson.toJson(task);
                   sendText(exchange, taskJson);
                   break;
           }
        }
    }

    @Override
    public void handlePostMethod(HttpExchange exchange) throws IOException {
        try (exchange) {
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
            Task task = gson.fromJson(reader, Task.class);
            if (task.getId() == null || task.getId() == 0) {
                try {
                    taskManager.createNewTask(task);
                    sendTextModification(exchange, "Задача успешно создана");
                } catch(ValidationException e) {
                    sendHasInteractions(exchange, e.getMessage());
                }
            } else {
                try {
                    taskManager.updateTask(task);
                    sendTextModification(exchange, "Задача успешно обновлена");
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
                        taskManager.clearListOfTasks();
                        sendText(exchange, "Все задачи удалены");
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    }
                    break;
                case 3:
                    int idTask = Integer.parseInt(pathParts[2]);
                    try {
                        taskManager.deleteTaskById(idTask);
                        sendText(exchange, "Задача успешно удалена");
                    } catch (NotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    }
                    break;
            }
        }
    }
}
