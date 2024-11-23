import enums.Status;
import exceptions.ValidationException;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class TaskManagerTest <T extends TaskManager> {

    T manager;

    protected abstract T createManager();

    @BeforeEach
    public void init() throws IOException {
        manager = createManager();
    }

    @Test
    public void createdTaskEqualsSavedTaskIfIdsIdentical() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewTask(task1);

        Task savedTask = manager.getTask(task1.getId());

        Assertions.assertEquals(task1, savedTask, "Задачи не совпадают");

    }

    @Test
    public void createdEpicEqualsSavedEpicIfIdsIdentical() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createNewEpic(epic1);

        Epic savedEpic = manager.getEpic(epic1.getId());

        Assertions.assertEquals(epic1, savedEpic, "Задачи не совпадают");

    }

    @Test
    public void createdSubtaskEqualsSavedSubtaskIfIdsIdentical() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createNewEpic(epic1);

        Subtask subtask1 = new Subtask(0, "Сабтаск 1", "Описание сабтаска 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask1);

        Subtask savedSubtask = manager.getSubtask(subtask1.getId());

        Assertions.assertEquals(subtask1, savedSubtask, "Сабтаски не совпадают");

    }

    @Test
    public void taskConflictIds() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));

        manager.createNewTask(task1);
        manager.createNewTask(task2);

        Assertions.assertNotEquals(task1, task2);
    }

    @Test
    public void epicExistsForSubtask() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createNewEpic(epic1);

        Subtask subtask1 = new Subtask(0, "Сабтаск 1", "Описание сабтаска 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask1);

        Assertions.assertEquals(epic1.getId(), subtask1.getEpicId());
    }

    @Test
    public void immutabilityTaskTest() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewTask(task1);

        Task updatedTask = new Task(task1.getId(), task1.getName(), task1.getDescription(), Status.IN_PROGRESS,
                task1.getStartTime(), task1.getDuration());
        manager.updateTask(updatedTask);

        Assertions.assertEquals(task1.getName(), updatedTask.getName());
        Assertions.assertEquals(task1.getDescription(), updatedTask.getDescription());
        Assertions.assertEquals(task1.getId(), updatedTask.getId());
    }

    @Test
    public void clearListOfTasksTest() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewTask(task1);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        manager.createNewTask(task2);

        manager.clearListOfTasks();

        Assertions.assertEquals(0, manager.getListOfTasks().size());
    }

    @Test
    public void clearListOfEpicsTest() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createNewEpic(epic1);

        Subtask subtask1 = new Subtask(0, "Сабтаск 1", "Описание сабтаска 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask1);

        manager.clearListOfEpics();

        Assertions.assertEquals(0, manager.getListOfEpics().size());
        Assertions.assertEquals(0, manager.getListOfSubtasks().size());
    }

    @Test
    public void clearListOfSubtasksTest() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createNewEpic(epic1);

        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(0, "Подзадача 2", "Описание подзадачи 2",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask2);

        manager.clearListOfSubtasks();

        Assertions.assertEquals(0, manager.getListOfSubtasks().size());
        Assertions.assertEquals(1, manager.getListOfEpics().size());
    }

    @Test
    public void updateEpicStatusTestIfSubtasksNew() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createNewEpic(epic1);

        Assertions.assertEquals(Status.NEW, epic1.getStatus());

        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(0, "Подзадача 2", "Описание подзадачи 2",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask2);

        Assertions.assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    public void updateEpicStatusTestIfSubtasksDone() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createNewEpic(epic1);

        Assertions.assertEquals(Status.NEW, epic1.getStatus());

        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(0, "Подзадача 2", "Описание подзадачи 2",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask2);

        Subtask updatedSubtask1 = new Subtask(subtask1.getId(), subtask1.getName(), subtask1.getDescription(),
                Status.DONE, subtask1.getEpicId(), subtask1.getStartTime(), subtask1.getDuration());
        manager.updateSubtask(updatedSubtask1);
        Subtask updatedSubtask2 = new Subtask(subtask2.getId(), subtask2.getName(), subtask2.getDescription(),
                Status.DONE, subtask2.getEpicId(), subtask2.getStartTime(), subtask2.getDuration());
        manager.updateSubtask(updatedSubtask2);

        Assertions.assertEquals(Status.DONE, epic1.getStatus());
    }

    @Test
    public void updateEpicStatusTestIfOneSubtasksInProgress() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createNewEpic(epic1);

        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(0, "Подзадача 2", "Описание подзадачи 2",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask2);

        Subtask updatedSubtask1 = new Subtask(subtask1.getId(), subtask1.getName(), subtask1.getDescription(),
                Status.IN_PROGRESS, subtask1.getEpicId(), subtask1.getStartTime(), subtask1.getDuration());
        manager.updateSubtask(updatedSubtask1);
        Subtask updatedSubtask2 = new Subtask(subtask2.getId(), subtask2.getName(), subtask2.getDescription(),
                Status.IN_PROGRESS, subtask2.getEpicId(), subtask2.getStartTime(), subtask2.getDuration());
        manager.updateSubtask(updatedSubtask2);

        Assertions.assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    public void updateEpicStatusTestIfOneSubtaskDone() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createNewEpic(epic1);

        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(0, "Подзадача 2", "Описание подзадачи 2",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask2);

        Subtask updatedSubtask1 = new Subtask(subtask1.getId(), subtask1.getName(), subtask1.getDescription(),
                Status.DONE, subtask1.getEpicId(), subtask1.getStartTime(), subtask1.getDuration());
        manager.updateSubtask(updatedSubtask1);

        Assertions.assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    public void updateTaskStatusTest() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewTask(task1);

        Task updatedTask1 = new Task(task1.getId(), "Задача 1", "Описание задачи 1", Status.IN_PROGRESS,
                task1.getStartTime(), task1.getDuration());
        manager.updateTask(updatedTask1);

        Assertions.assertEquals(Status.IN_PROGRESS, updatedTask1.getStatus());
    }

    @Test
    public void deleteTaskTest() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewTask(task1);

        manager.deleteTaskById(task1.getId());

        Assertions.assertEquals(0, manager.getListOfTasks().size());
    }


    @Test
    public void deleteEpicTest() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createNewEpic(epic1);

        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewTask(subtask1);
        Subtask subtask2 = new Subtask(0, "Подзадача 2", "Описание подзадачи 2",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        manager.createNewTask(subtask2);

        manager.deleteEpicById(epic1.getId());

        Assertions.assertEquals(0, manager.getListOfEpics().size());
        Assertions.assertEquals(0, manager.getListOfSubtasks().size());
    }

    @Test
    public void deleteSubtaskTest() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createNewEpic(epic1);

        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(0, "Подзадача 2", "Описание подзадачи 2",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        manager.createNewSubtask(subtask2);

        Assertions.assertEquals(2, manager.getListOfSubtasks().size());

        manager.deleteSubtaskById(subtask1.getId());

        Assertions.assertEquals(1, manager.getListOfSubtasks().size());
        Assertions.assertEquals(1, epic1.getListOfSubtasksId().size());

    }

    @Test
    public void addNewTaskWithTimeConflict() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        manager.createNewTask(task1);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 13), Duration.ofMinutes(15));

        Assertions.assertThrows(ValidationException.class, () -> {
            manager.createNewTask(task2);
        });
    }
}
