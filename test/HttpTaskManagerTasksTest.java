import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import enums.Status;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import server.HttpTaskServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import typeAdapters.DurationTypeAdapter;
import typeAdapters.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManagerTasksTest {

    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer();
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    @BeforeEach
    public void setUp() throws IOException {
        taskServer.start(taskManager);
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    public HttpResponse<String> postData(URI url, String taskJson) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> getData(URI url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> deleteData(URI url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));

        String taskJson = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpResponse<String> response = postData(url, taskJson);

        Assertions.assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getListOfTasks();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Задача 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));

        String taskJson = gson.toJson(task1);
        URI url = URI.create("http://localhost:8080/tasks");
        postData(url, taskJson);

        Task task2 = taskManager.getTask(1);
        task2.setName("Задача 2");
        String task2Json = gson.toJson(task2);
        HttpResponse<String> response2 = postData(url, task2Json);

        Assertions.assertEquals(201, response2.statusCode());

        List<Task> tasksFromManager = taskManager.getListOfTasks();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Задача 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));

        String taskJson = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks");
        postData(url, taskJson);

        URI url2 = URI.create("http://localhost:8080/tasks/1");
        HttpResponse<String> response = getData(url2);
        Task task2 = taskManager.getTask(1);
        String task2Json = gson.toJson(task2);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(response.body(), task2Json, "Задачи не совпадают");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));

        String task1Json = gson.toJson(task1);
        String task2Json = gson.toJson(task2);
        URI url = URI.create("http://localhost:8080/tasks");
        postData(url, task1Json);
        postData(url, task2Json);

        HttpResponse<String> response = getData(url);
        ArrayList<Task> tasksList = taskManager.getListOfTasks();
        String tasksListJson = gson.toJson(tasksList);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(response.body(), tasksListJson, "Списки задач не совпадают");
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));

        String task1Json = gson.toJson(task1);
        URI url = URI.create("http://localhost:8080/tasks");
        postData(url, task1Json);

        URI url2 = URI.create("http://localhost:8080/tasks/1");
        HttpResponse<String> response = deleteData(url2);
        Assertions.assertEquals(200, response.statusCode());

        ArrayList<Task> tasksList = taskManager.getListOfTasks();
        String tasksListJson = gson.toJson(tasksList);

        Assertions.assertEquals("[]",tasksListJson, "Список задач не пустой");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);
        Subtask subtask = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 32), Duration.ofMinutes(15));

        String subtaskJson = gson.toJson(subtask);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpResponse<String> response = postData(url, subtaskJson);

        Assertions.assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getListOfSubtasks();

        Assertions.assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        Assertions.assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        Assertions.assertEquals("Подзадача 1", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);
        Subtask subtask = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 32), Duration.ofMinutes(15));

        String subtaskJson = gson.toJson(subtask);
        URI url = URI.create("http://localhost:8080/subtasks");
        postData(url, subtaskJson);

        Subtask subtask2 = taskManager.getSubtask(2);
        subtask2.setName("Подзадача 2");
        String subtask2Json = gson.toJson(subtask2);
        HttpResponse<String> response2 = postData(url, subtask2Json);

        Assertions.assertEquals(201, response2.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getListOfSubtasks();

        Assertions.assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        Assertions.assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        Assertions.assertEquals("Подзадача 2", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);
        Subtask subtask = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 32), Duration.ofMinutes(15));

        String subtaskJson = gson.toJson(subtask);
        URI url = URI.create("http://localhost:8080/subtasks");
        postData(url, subtaskJson);

        URI url2 = URI.create("http://localhost:8080/subtasks/2");
        HttpResponse<String> response = getData(url2);
        Subtask subtask2 = taskManager.getSubtask(2);
        String subtask2Json = gson.toJson(subtask2);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(response.body(), subtask2Json, "Подзадачи не совпадают");
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);
        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 32), Duration.ofMinutes(15));
        Subtask subtask2 = new Subtask(0, "Подзадача 2", "Описание подзадачи 2",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 50), Duration.ofMinutes(15));

        String subtask1Json = gson.toJson(subtask1);
        String subtask2Json = gson.toJson(subtask2);
        URI url = URI.create("http://localhost:8080/subtasks");
        postData(url, subtask1Json);
        postData(url, subtask2Json);

        HttpResponse<String> response = getData(url);
        ArrayList<Subtask> subtasksList = taskManager.getListOfSubtasks();
        String subtasksListJson = gson.toJson(subtasksList);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(response.body(), subtasksListJson, "Списки подзадач не совпадают");
    }

    @Test
    public void testDeleteSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);
        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 32), Duration.ofMinutes(15));

        String subtask1Json = gson.toJson(subtask1);
        URI url = URI.create("http://localhost:8080/subtasks");
        postData(url, subtask1Json);

        URI url2 = URI.create("http://localhost:8080/subtasks/2");
        HttpResponse<String> response = deleteData(url2);
        Assertions.assertEquals(200, response.statusCode());

        ArrayList<Subtask> subtasksList = taskManager.getListOfSubtasks();
        String subtasksListJson = gson.toJson(subtasksList);

        Assertions.assertEquals("[]",subtasksListJson, "Список подзадач не пустой");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic(1, "Эпик 1", "Описание эпика 1", Status.NEW);

        String epicJson = gson.toJson(epic1);
        URI url = URI.create("http://localhost:8080/epics");
        HttpResponse<String> response = postData(url, epicJson);

        Assertions.assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = taskManager.getListOfEpics();

        Assertions.assertNotNull(epicsFromManager, "Эпики не возвращаются");
        Assertions.assertEquals(1, epicsFromManager.size(), "Некорректное количество эпики");
        Assertions.assertEquals("Эпик 1", epicsFromManager.get(0).getName(), "Некорректное имя эпики");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");

        String epicJson = gson.toJson(epic1);
        URI url = URI.create("http://localhost:8080/epics");
        postData(url, epicJson);

        URI url2 = URI.create("http://localhost:8080/epics/1");
        HttpResponse<String> response = getData(url2);
        Epic epic2 = taskManager.getEpic(1);
        String epic2Json = gson.toJson(epic2);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(response.body(), epic2Json, "Эпики не совпадают");
    }

    @Test
    public void testGetSubtasksByEpicId() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);
        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 32), Duration.ofMinutes(15));
        Subtask subtask2 = new Subtask(0, "Подзадача 2", "Описание подзадачи 2",
                Status.NEW, epic1.getId(),
                LocalDateTime.of(2024,11,10, 22, 50), Duration.ofMinutes(15));
        taskManager.createNewSubtask(subtask1);
        taskManager.createNewSubtask(subtask2);

        URI url2 = URI.create("http://localhost:8080/epics/" + epic1.getId() + "/subtasks");
        HttpResponse<String> response = getData(url2);
        ArrayList<Subtask> listSubtasksByEpicId = taskManager.getListOfSubtasksByEpic(epic1.getId());
        String listSubtasksByEpicIdJson = gson.toJson(listSubtasksByEpicId);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(response.body(), listSubtasksByEpicIdJson, "Списки подзадач не совпадают");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");

        String epic1Json = gson.toJson(epic1);
        String epic2Json = gson.toJson(epic2);
        URI url = URI.create("http://localhost:8080/epics");
        postData(url, epic1Json);
        postData(url, epic2Json);

        HttpResponse<String> response = getData(url);
        ArrayList<Epic> epicsList = taskManager.getListOfEpics();
        String epicsListJson = gson.toJson(epicsList);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(response.body(), epicsListJson, "Списки эпиков не совпадают");
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");

        String epic1Json = gson.toJson(epic1);
        URI url = URI.create("http://localhost:8080/epics");
        postData(url, epic1Json);

        URI url2 = URI.create("http://localhost:8080/epics/1");
        HttpResponse<String> response = deleteData(url2);
        Assertions.assertEquals(200, response.statusCode());

        ArrayList<Epic> epicsList = taskManager.getListOfEpics();
        String epicsListJson = gson.toJson(epicsList);

        Assertions.assertEquals("[]",epicsListJson, "Список эпиков не пустой");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 0), Duration.ofMinutes(15));

        String task1Json = gson.toJson(task1);
        URI url = URI.create("http://localhost:8080/tasks");
        postData(url, task1Json);

        URI url2 = URI.create("http://localhost:8080/tasks/1");
        getData(url2);
        URI url3 = URI.create("http://localhost:8080/history");

        HttpResponse<String> response = getData(url3);
        List<Task> tasksList = taskManager.getHistory();
        String tasksListJson = gson.toJson(tasksList);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(response.body(), tasksListJson, "История задач не совпадает");
    }

    @Test
    public void testGetPrioritizedListOfTasks() throws IOException, InterruptedException {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 40), Duration.ofMinutes(15));
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2024,11,10, 22, 16), Duration.ofMinutes(15));

        String task1Json = gson.toJson(task1);
        String task2Json = gson.toJson(task2);
        URI url = URI.create("http://localhost:8080/tasks");
        postData(url, task1Json);
        postData(url, task2Json);

        URI url2 = URI.create("http://localhost:8080/prioritized");

        HttpResponse<String> response = getData(url2);
        ArrayList<Task> prioritizedList = taskManager.getPrioritizedTasks();
        String prioritizedListJson = gson.toJson(prioritizedList);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(response.body(), prioritizedListJson, "Списки задач по приоритету не совпадают");
    }
}