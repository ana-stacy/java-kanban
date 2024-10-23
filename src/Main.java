import manager.FileBackedTaskManager;
import tasks.Epic;
import tasks.Task;
import tasks.Subtask;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println("Поехали!");

        File file = File.createTempFile("tempFile", ".csv");
        FileBackedTaskManager fileBackedManager = FileBackedTaskManager.loadFromFile(file);

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        fileBackedManager.createNewTask(task1);
        Task task2 = new Task("Задача 12", "Описание задачи 2");
        fileBackedManager.createNewTask(task2);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        fileBackedManager.createNewEpic(epic1);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        fileBackedManager.createNewEpic(epic2);
        Subtask subtask1 = new Subtask("Сабтаск 1", "Описание сабтаска 1", epic1.getId());
        fileBackedManager.createNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Описание сабтаска 2", epic1.getId());
        fileBackedManager.createNewSubtask(subtask2);

        System.out.println("Задачи:");
        for (Task task : fileBackedManager.getListOfTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : fileBackedManager.getListOfEpics()) {
            System.out.println(epic);

            for (Integer subtaskId : epic.getListOfSubtasksId()) {
                Subtask subtask = fileBackedManager.getSubtask(subtaskId);
                System.out.println("--> " + subtask);
            }
        }
        System.out.println("Подзадачи:");
        for (Subtask subtask : fileBackedManager.getListOfSubtasks()) {
            System.out.println(subtask);
        }

        FileBackedTaskManager loadFileManager = FileBackedTaskManager.loadFromFile(file);

        Task task3 = new Task("Задача 3", "Описание задачи 3");
        loadFileManager.createNewTask(task3);

        System.out.println("Список задач из файла:");
        System.out.println("Задачи:");
        for (Task task : loadFileManager.getListOfTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : loadFileManager.getListOfEpics()) {
            System.out.println(epic);

            for (Integer subtaskId : epic.getListOfSubtasksId()) {
                Subtask subtask = loadFileManager.getSubtask(subtaskId);
                System.out.println("--> " + subtask);
            }
        }
        System.out.println("Подзадачи:");
        for (Subtask subtask : loadFileManager.getListOfSubtasks()) {
            System.out.println(subtask);
        }
    }
}
