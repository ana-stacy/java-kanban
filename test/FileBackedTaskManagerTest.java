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

public class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {

    private File file;
    private FileBackedTaskManager taskManager;


    @BeforeEach
    public void init() throws IOException {
        file = File.createTempFile("tempFile", ".csv");
        taskManager = FileBackedTaskManager.loadFromFile(file);
    }

    @AfterEach
    public void closeFile() {
        boolean deleted = file.delete();
        if (!deleted) {
            System.out.println("Не удалось удалить временный файл");
        }
    }

    @Test
    public void saveIfTaskCreatedTest() throws IOException {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.createNewTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.createNewTask(task2);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);
        Subtask subtask1 = new Subtask("Сабтаск 1", "Описание сабтаска 1", epic1.getId());
        taskManager.createNewSubtask(subtask1);

        BufferedReader reader  = new BufferedReader(new FileReader(file));

        StringBuilder stringBuilder = new StringBuilder();

        while (reader.ready()) {
            String line = reader.readLine();
            stringBuilder.append(line);
        }

        String actualOutput = stringBuilder.toString();
        reader.close();

        String expectedOutput = "id,type,name,status,description,epic" +
                "1,TASK,Задача 1,NEW,Описание задачи 1," +
                "2,TASK,Задача 2,NEW,Описание задачи 2," +
                "3,EPIC,Эпик 1,NEW,Описание эпика 1," +
                "4,SUBTASK,Сабтаск 1,NEW,Описание сабтаска 1,3";

        Assertions.assertEquals(expectedOutput, actualOutput, "Содержимое файлов не совпадает");
    }

    @Test
    public void updateIdCounterTest() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.createNewTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.createNewTask(task2);

        FileBackedTaskManager loadFileManager = FileBackedTaskManager.loadFromFile(file);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        loadFileManager.createNewEpic(epic1);

        Assertions.assertEquals(3, epic1.getId());
    }

    @Test
    public void loadFromFileTest() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.createNewTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);
        Subtask subtask1 = new Subtask("Сабтаск 1", "Описание сабтаска 1", epic1.getId());
        taskManager.createNewSubtask(subtask1);

        FileBackedTaskManager loadFileManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(taskManager.getListOfTasks(), loadFileManager.getListOfTasks(), "Задачи не совпадают");
    }

    /*@Override
    @Test
    public void createdTaskEqualsSavedTaskIfIdsIdentical() {
        super.createdTaskEqualsSavedTaskIfIdsIdentical();

    }

    @Override
    @Test
    public void createdEpicEqualsSavedEpicIfIdsIdentical() {
        super.createdEpicEqualsSavedEpicIfIdsIdentical();
    }

    @Override
    @Test
    public void createdSubtaskEqualsSavedSubtaskIfIdsIdentical() {
        super.createdSubtaskEqualsSavedSubtaskIfIdsIdentical();
    }

    @Override
    @Test
    public void taskConflictIds() {
        super.taskConflictIds();
    }

    @Override
    @Test
    public void immutabilityTaskTest() {
        super.immutabilityTaskTest();
    }

    @Override
    @Test
    public void clearListOfTasksTest() {
        super.clearListOfTasksTest();
    }

    @Override
    @Test
    public void clearListOfEpicsTest() {
        super.clearListOfEpicsTest();
    }

    @Override
    @Test
    public void clearListOfSubtasksTest() {
        super.clearListOfSubtasksTest();
    }

    @Override
    @Test
    public void updateEpicStatusTest() {
        super.updateEpicStatusTest();
    }

    @Override
    @Test
    public void updateTaskStatusTest() {
        super.updateTaskStatusTest();
    }

    @Override
    @Test
    public void deleteTaskTest() {
        super.deleteTaskTest();
    }

    @Override
    @Test
    public void deleteEpicTest() {
        super.deleteEpicTest();
    }

    @Override
    @Test
    public void deleteSubtaskTest() {
        super.deleteSubtaskTest();
    }*/

}
