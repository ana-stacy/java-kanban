import TaskManager.TaskManager;
import Tasks.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();


        // Создайте две задачи, а также эпик с двумя подзадачами и эпик с одной подзадачей.
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");

        taskManager.createNewTask(task1);
        taskManager.createNewTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");

        taskManager.createNewEpic(epic1);
        taskManager.createNewEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());

        taskManager.createNewSubtask(subtask1);
        taskManager.createNewSubtask(subtask2);

        // Распечатайте списки эпиков, задач и подзадач через
        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubtasks());

        //Измените статусы созданных объектов, распечатайте их.
        // Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        Task task3 = new Task(task1.getId(), "Задача 1", "Описание задачи 1", Status.IN_PROGRESS);
        taskManager.updateTask(task3);

        Subtask subtask3 = new Subtask(subtask1.getId(), "Подзадача 1", "Описание подзадачи 1",
                Status.IN_PROGRESS, epic1.getId());
        taskManager.updateSubtask(subtask3);

        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubtasks());

        // И, наконец, попробуйте удалить одну из задач и один из эпиков.
        taskManager.deleteEpicById(epic1.getId());
        taskManager.deleteTaskById(task3.getId());

        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubtasks());


    }
}
