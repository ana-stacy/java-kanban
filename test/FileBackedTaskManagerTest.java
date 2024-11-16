import enums.Status;
import manager.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File file;

    @Override
    public FileBackedTaskManager createManager() {
        return FileBackedTaskManager.loadFromFile(file);
    }

    @BeforeEach
    public void init() throws IOException {
        file = File.createTempFile("tempFile", ".csv");
        manager = createManager();
    }

    @AfterEach
    public void closeFile() {
        boolean deleted = file.delete();
        Assertions.assertTrue(deleted, "Не удалось удалить временный файл: " + file.getAbsolutePath());
    }

    @Test
    public void saveIfTaskCreatedTest() throws IOException {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewTask(task1);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        manager.createNewTask(task2);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createNewEpic(epic1);
        Subtask subtask1 = new Subtask(0, "Сабтаск 1", "Описание сабтаска 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 40), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask1);

        BufferedReader reader  = new BufferedReader(new FileReader(file));

        StringBuilder stringBuilder = new StringBuilder();

        while (reader.ready()) {
            String line = reader.readLine();
            stringBuilder.append(line);
        }

        String actualOutput = stringBuilder.toString();
        reader.close();

        String expectedOutput = "id,type,name,status,description,epic,startTime,endTime,duration" +
                "1,TASK,Задача 1,NEW,Описание задачи 1,null,2024-11-10T22:00,2024-11-10T22:15,PT15M," +
                "2,TASK,Задача 2,NEW,Описание задачи 2,null,2024-11-10T22:16,2024-11-10T22:31,PT15M," +
                "3,EPIC,Эпик 1,NEW,Описание эпика 1,null,2024-11-10T22:40,2024-11-10T22:55,PT15M," +
                "4,SUBTASK,Сабтаск 1,NEW,Описание сабтаска 1,3,2024-11-10T22:40,2024-11-10T22:55,PT15M,";

        Assertions.assertEquals(expectedOutput, actualOutput, "Содержимое файлов не совпадает");
    }

    @Test
    public void updateIdCounterTest() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewTask(task1);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        manager.createNewTask(task2);

        FileBackedTaskManager loadFileManager = FileBackedTaskManager.loadFromFile(file);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        loadFileManager.createNewEpic(epic1);

        Assertions.assertEquals(3, epic1.getId());
    }

    @Test
    public void loadFromFileTest() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createNewEpic(epic1);
        Subtask subtask1 = new Subtask(0, "Сабтаск 1", "Описание сабтаска 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask1);

        FileBackedTaskManager loadFileManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(manager.getListOfTasks(), loadFileManager.getListOfTasks(), "Задачи не совпадают");
        Assertions.assertEquals(manager.getListOfSubtasks(), loadFileManager.getListOfSubtasks(), "Подзадачи не совпадают");
        Assertions.assertEquals(manager.getListOfEpics(), loadFileManager.getListOfEpics(), "Эпики не совпадают");
    }
}
