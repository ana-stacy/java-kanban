import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import taskmanagers.*;
import tasks.*;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void createdTaskEqualsSavedTaskIfIdsIdentical() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.createNewTask(task1);

        Task savedTask = taskManager.getTask(task1.getId());

        Assertions.assertEquals(task1, savedTask, "Задачи не совпадают");

    }

    @Test
    public void createdEpicEqualsSavedEpicIfIdsIdentical() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);

        Epic savedEpic = taskManager.getEpic(epic1.getId());

        Assertions.assertEquals(epic1, savedEpic, "Задачи не совпадают");

    }

    @Test
    public void createdSubtaskEqualsSavedSubtaskIfIdsIdentical() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Описание сабтаска 1", epic1.getId());
        taskManager.createNewSubtask(subtask1);

        Subtask savedSubtask = taskManager.getSubtask(subtask1.getId());

        Assertions.assertEquals(subtask1, savedSubtask, "Сабтаски не совпадают");

    }

    @Test
    public void taskConflictIds() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");

        taskManager.createNewTask(task1);
        taskManager.createNewTask(task2);

        Assertions.assertNotEquals(task1, task2);
    }

    @Test
    public void immutabilityTaskTest() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.createNewTask(task1);

        Task updatedTask = new Task(task1.getId(), task1.getName(), task1.getDescription(), Status.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        Assertions.assertEquals(task1.getName(), updatedTask.getName());
        Assertions.assertEquals(task1.getDescription(), updatedTask.getDescription());
        Assertions.assertEquals(task1.getId(), updatedTask.getId());
    }

    @Test
    public void clearListOfTasksTest() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.createNewTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.createNewTask(task2);

        taskManager.clearListOfTasks();

        Assertions.assertEquals(0, taskManager.getListOfTasks().size());
    }

    @Test
    public void clearListOfEpicsTest() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Описание сабтаска 1", epic1.getId());
        taskManager.createNewSubtask(subtask1);

        taskManager.clearListOfEpics();

        Assertions.assertEquals(0, taskManager.getListOfEpics().size());
        Assertions.assertEquals(0, taskManager.getListOfSubtasks().size());
    }

    @Test
    public void clearListOfSubtasksTest() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        taskManager.createNewTask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        taskManager.createNewTask(subtask2);

        taskManager.clearListOfSubtasks();

        Assertions.assertEquals(0, taskManager.getListOfSubtasks().size());
        Assertions.assertEquals(1, taskManager.getListOfEpics().size());
    }

    @Test
    public void updateEpicStatusTest() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);

        Assertions.assertEquals(Status.NEW, epic1.getStatus());

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        taskManager.createNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
        taskManager.createNewSubtask(subtask2);

        Subtask updatedSubtask1 = new Subtask(subtask1.getId(), "Подзадача 1", "Описание подзадачи 1",
                Status.IN_PROGRESS, epic1.getId());
        taskManager.updateSubtask(updatedSubtask1);

        Assertions.assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    public void updateTaskStatusTest() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.createNewTask(task1);

        Task updatedTask1 = new Task(task1.getId(), "Задача 1", "Описание задачи 1", Status.IN_PROGRESS);
        taskManager.updateTask(updatedTask1);

        Assertions.assertEquals(Status.IN_PROGRESS, updatedTask1.getStatus());
    }

    @Test
    public void deleteTaskTest() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.createNewTask(task1);

        taskManager.deleteTaskById(task1.getId());

        Assertions.assertEquals(0, taskManager.getListOfTasks().size());
    }


    @Test
    public void deleteEpicTest() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        taskManager.createNewTask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        taskManager.createNewTask(subtask2);

        taskManager.deleteEpicById(epic1.getId());

        Assertions.assertEquals(0, taskManager.getListOfEpics().size());
        Assertions.assertEquals(0, taskManager.getListOfSubtasks().size());
    }

    @Test
    public void deleteSubtaskTest() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        taskManager.createNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        taskManager.createNewSubtask(subtask2);

        Assertions.assertEquals(2, taskManager.getListOfSubtasks().size());

        taskManager.deleteSubtaskById(subtask1.getId());

        Assertions.assertEquals(1, taskManager.getListOfSubtasks().size());
        Assertions.assertEquals(1, epic1.getListOfSubtasksId().size());

    }

}
