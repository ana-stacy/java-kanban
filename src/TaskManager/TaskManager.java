package TaskManager;

import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;

    int idCounter = 0;

    public TaskManager () {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    // a. Получение списка всех задач.
    public ArrayList<Task> getListOfTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getListOfEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getListOfSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // b. Удаление всех задач.
    public void clearListOfTasks() {
        tasks.clear();
    }

    public void clearListOfEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void clearListOfSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearListOfSubtasksByEpic();
            updateEpic(epic.getId());
        }
    }

    // c. Получение по идентификатору.
    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    public Epic getEpic(int epicId) {
        return epics.get(epicId);
    }

    public Subtask getSubtask(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    public void createNewTask (Task task) {
        task.setId(++idCounter);
        tasks.put(task.getId(), task);
    }

    public void createNewEpic (Epic epic) {
        epic.setId(++idCounter);
        epics.put(epic.getId(), epic);
    }

    public void createNewSubtask (Subtask subtask) {
        subtask.setId(++idCounter);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(idCounter);
        updateEpic(epic.getId());
    }

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask (Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic (int epicId) {
        Epic epic = epics.get(epicId);
        epic.setStatus(defineEpicStatus(getListOfSubtasksByEpic(epicId)));
    }

    private Status defineEpicStatus(ArrayList<Subtask> listOfSubtasksByEpic) {
        ArrayList<Status> statuses = checkUniqueSubtasksStatuses(listOfSubtasksByEpic);
        if (statuses.size() == 1 && statuses.contains(Status.NEW) || statuses.isEmpty()) {
            return Status.NEW;
        } else if (statuses.size() == 1 && statuses.contains(Status.DONE)) {
            return Status.DONE;
        } else {
            return Status.IN_PROGRESS;
        }
    }

    private ArrayList<Status> checkUniqueSubtasksStatuses(ArrayList<Subtask> listOfSubtasksByEpic) {
        ArrayList<Status> statuses = new ArrayList<>();
        for (Subtask subtask : listOfSubtasksByEpic) {
            Status status = subtask.getStatus();
            boolean isContainsStatus = statuses.contains(status);
            if (!isContainsStatus) {
                statuses.add(status);
            }
        }
        return statuses;
    }

    public void updateSubtask (Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpic(subtask.getEpicId());
    }

    //f. Удаление по идентификатору.
    public void deleteTaskById (int taskId) {
        tasks.remove(taskId);
    }

    public void deleteEpicById (int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getListOfSubtasksId()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(epicId);
        }
    }

    public void deleteSubtaskById (int subtaskId) {
        Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
        epic.deleteSubtaskId(subtaskId);
        subtasks.remove(subtaskId);
        updateEpic(epic.getId());
    }

    // Дополнительные методы:
    //a. Получение списка всех подзадач определённого эпика.
    ArrayList<Subtask> getListOfSubtasksByEpic (int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        for (int subtaskId : epic.getListOfSubtasksId()) {
            Subtask subtask = subtasks.get(subtaskId);
            listOfSubtasks.add(subtask);
        }
        return listOfSubtasks;
    }
}

