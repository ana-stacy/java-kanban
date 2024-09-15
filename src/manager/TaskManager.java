package manager;

import tasks.Epic;
import tasks.Task;
import tasks.Subtask;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    ArrayList<Task> getListOfTasks();

    ArrayList<Epic> getListOfEpics();

    ArrayList<Subtask> getListOfSubtasks();

    void clearListOfTasks();

    void clearListOfEpics();

    void clearListOfSubtasks();

    Task getTask(int taskId);

    Epic getEpic(int epicId);

    Subtask getSubtask(int subtaskId);

    void createNewTask(Task task);

    void createNewEpic(Epic epic);

    void createNewSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(int taskId);

    void deleteEpicById(int epicId);

    void deleteSubtaskById(int subtaskId);

    ArrayList<Subtask> getListOfSubtasksByEpic(int epicId);

    List<Task> getHistory();
}
