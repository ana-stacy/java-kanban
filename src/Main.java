import taskManagers.*;
import tasks.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");

        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.createNewTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.createNewTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createNewEpic(epic1);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createNewEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        taskManager.createNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
        taskManager.createNewSubtask(subtask2);

        Task updatedTask1 = new Task(task1.getId(), "Задача 1", "Описание задачи 1", Status.IN_PROGRESS);
        taskManager.updateTask(updatedTask1);

        Subtask updatedSubtask1 = new Subtask(subtask1.getId(), "Подзадача 1", "Описание подзадачи 1",
                Status.IN_PROGRESS, epic1.getId());
        taskManager.updateSubtask(updatedSubtask1);


        System.out.println("Задачи:");
        for (Task task : taskManager.getListOfTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : taskManager.getListOfEpics()) {
            System.out.println(epic);

            for (Integer subtaskId : epic.getListOfSubtasksId()) {
                Subtask subtask = taskManager.getSubtask(subtaskId);
                System.out.println("--> " + subtask);
            }
        }
        System.out.println("Подзадачи:");
        for (Subtask subtask : taskManager.getListOfSubtasks()) {
            System.out.println(subtask);
        }


        taskManager.getTask(task1.getId());
        taskManager.getSubtask(subtask1.getId());

        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

    }
}
