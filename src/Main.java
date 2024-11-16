import enums.Status;
import manager.FileBackedTaskManager;
import tasks.Epic;
import tasks.Task;
import tasks.Subtask;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println("Поехали!");

        File file = File.createTempFile("tempFile", ".csv");
        FileBackedTaskManager fileBackedManager = FileBackedTaskManager.loadFromFile(file);

        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        fileBackedManager.createNewTask(task1);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        fileBackedManager.createNewTask(task2);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        fileBackedManager.createNewEpic(epic1);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        fileBackedManager.createNewEpic(epic2);
        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 32), Duration.ofMinutes(15));
        fileBackedManager.createNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(0, "Подзадача 2", "Описание подзадачи 2",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 50), Duration.ofMinutes(15));
        fileBackedManager.createNewSubtask(subtask2);

        Subtask updatedSubtask1 = new Subtask(subtask1.getId(), subtask1.getName(), subtask1.getDescription(),
                Status.DONE, subtask1.getEpicId(),
                LocalDateTime.of(2024,11,10, 21, 32), subtask1.getDuration());
        fileBackedManager.updateSubtask(updatedSubtask1);


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

        FileBackedTaskManager loadFileManager = FileBackedTaskManager.loadFromFile(file);

        Task task3 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2024,11,10, 23, 20), Duration.ofMinutes(15));
        loadFileManager.createNewTask(task3);

        Subtask updatedSubtask2 = new Subtask(subtask2.getId(), subtask2.getName(), subtask2.getDescription(),
                Status.DONE, subtask2.getEpicId(),
                LocalDateTime.of(2024,11,10, 15, 32), subtask2.getDuration());
        loadFileManager.updateSubtask(updatedSubtask2);

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
    }
}
