import enums.Status;
import nodes.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import tasks.Epic;
import tasks.Task;
import tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManagerTest {

    private InMemoryTaskManager taskManager;
    private InMemoryHistoryManager.HandMadeLinkedList history;


    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
        history = new InMemoryHistoryManager.HandMadeLinkedList();
    }

    @Test
    public void addTaskInHistoryTest() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        taskManager.createNewTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);
        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        taskManager.createNewSubtask(subtask1);

        Assertions.assertEquals(0, taskManager.getHistory().size());

        taskManager.getTask(task1.getId());
        Assertions.assertEquals(1, taskManager.getHistory().size());

    }

    @Test
    public void removeTaskFromBeginningOfHistoryTest() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        taskManager.createNewTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);
        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        taskManager.createNewSubtask(subtask1);

        Assertions.assertEquals(0, taskManager.getHistory().size());

        taskManager.getTask(task1.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getEpic(epic1.getId());
        Assertions.assertEquals(3, taskManager.getHistory().size());

        taskManager.deleteTaskById(task1.getId());
        Assertions.assertEquals(2, taskManager.getHistory().size());
    }

    @Test
    public void removeTaskFromMiddleOfHistoryTest() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        taskManager.createNewTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);
        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        taskManager.createNewSubtask(subtask1);

        Assertions.assertEquals(0, taskManager.getHistory().size());

        taskManager.getTask(task1.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getEpic(epic1.getId());
        Assertions.assertEquals(3, taskManager.getHistory().size());

        taskManager.deleteSubtaskById(subtask1.getId());
        Assertions.assertEquals(2, taskManager.getHistory().size());
    }

    @Test
    public void removeTaskFromEndOfHistoryTest() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        taskManager.createNewTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        taskManager.createNewTask(task2);

        Assertions.assertEquals(0, taskManager.getHistory().size());

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic1.getId());
        Assertions.assertEquals(3, taskManager.getHistory().size());

        taskManager.deleteEpicById(epic1.getId());
        Assertions.assertEquals(2, taskManager.getHistory().size());
    }

    @Test
    public void updateHistoryIfTaskExistsInHistoryTest() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.createNewTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);

        Assertions.assertEquals(0, taskManager.getHistory().size());

        taskManager.getTask(task1.getId());
        Assertions.assertEquals(1, taskManager.getHistory().size());

        taskManager.getTask(task1.getId());
        Assertions.assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    public void linkLastTest() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        taskManager.createNewTask(task1);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        taskManager.createNewTask(task2);
        Task task3 = new Task(0, "Задача 3", "Описание задачи 3", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 40), Duration.ofMinutes(15));
        taskManager.createNewTask(task3);

        history.linklast(task3);
        history.linklast(task1);
        history.linklast(task2);

        List<Task> historyList = history.getTasks();

        Assertions.assertEquals(task3, historyList.get(0));
        Assertions.assertEquals(task1, historyList.get(1));
        Assertions.assertEquals(task2, historyList.get(2));
    }

    @Test
    public void linkLastIfTaskExistsTest() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        taskManager.createNewTask(task1);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        taskManager.createNewTask(task2);


        Assertions.assertEquals(0, history.getTasks().size());

        history.linklast(task2);
        history.linklast(task1);

        Assertions.assertEquals(2, history.getTasks().size());

        history.linklast(task2);

        List<Task> historyList = history.getTasks();

        Assertions.assertEquals(2, history.getTasks().size());
        Assertions.assertEquals(task2, historyList.get(1));

    }

    @Test
    public void NodesTest() {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        taskManager.createNewTask(task1);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));
        taskManager.createNewTask(task2);
        Task task3 = new Task(0, "Задача 3", "Описание задачи 3", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 40), Duration.ofMinutes(15));
        taskManager.createNewTask(task3);

        history.linklast(task2);
        history.linklast(task1);
        history.linklast(task3);

        Map<Integer, Node<Task>> historyMap = history.getHistoryMap();

        Node<Task> head = historyMap.get(task2.getId());
        Node<Task> middle = historyMap.get(task1.getId());
        Node<Task> tail = historyMap.get(task3.getId());

        Assertions.assertEquals(3, history.getTasks().size());
        Assertions.assertEquals(task1, head.getNext().getData());
        Assertions.assertEquals(task1, tail.getPrev().getData());
        Assertions.assertEquals(task3, middle.getNext().getData());
        Assertions.assertEquals(task2, middle.getPrev().getData());

        history.linklast(task1);

        middle = historyMap.get(task3.getId());
        tail = historyMap.get(task1.getId());

        Assertions.assertEquals(3, history.getTasks().size());
        Assertions.assertEquals(task3, head.getNext().getData());
        Assertions.assertEquals(task3, tail.getPrev().getData());
        Assertions.assertEquals(task1, middle.getNext().getData());
        Assertions.assertEquals(task2, middle.getPrev().getData());

    }


}
