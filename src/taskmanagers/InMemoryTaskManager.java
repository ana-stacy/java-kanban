package taskmanagers;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private HistoryManager historyManager = Managers.getDefaultHistory();
    int idCounter = 0;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public ArrayList<Task> getListOfTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getListOfEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getListOfSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearListOfTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void clearListOfEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearListOfSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearListOfSubtasksByEpic();
            defineEpicStatus(epic.getId());
        }
    }

    @Override
    public Task getTask(int taskId) {
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpic(int epicId) {
        historyManager.add((Task) epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        historyManager.add((Task) subtasks.get(subtaskId));
        return subtasks.get(subtaskId);
    }

    @Override
    public void createNewTask(Task task) {
        task.setId(++idCounter);
        tasks.put(task.getId(), task);
    }

    @Override
    public void createNewEpic(Epic epic) {
        epic.setId(++idCounter);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        subtask.setId(++idCounter);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(idCounter);
        defineEpicStatus(epic.getId());
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    private void defineEpicStatus(int epicId) {
        ArrayList<Status> statuses = checkUniqueSubtasksStatuses(getListOfSubtasksByEpic(epicId));
        if (statuses.size() == 1 && statuses.contains(Status.NEW) || statuses.isEmpty()) {
            epics.get(epicId).setStatus(Status.NEW);
        } else if (statuses.size() == 1 && statuses.contains(Status.DONE)) {
            epics.get(epicId).setStatus(Status.DONE);
        } else {
            epics.get(epicId).setStatus(Status.IN_PROGRESS);
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

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        defineEpicStatus(subtask.getEpicId());
    }

    @Override
    public void deleteTaskById(int taskId) {
        historyManager.remove(taskId);
        tasks.remove(taskId);
    }

    @Override
    public void deleteEpicById(int epicId) {
        historyManager.remove(epicId);
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getListOfSubtasksId()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epics.remove(epicId);
        }
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        historyManager.remove(subtaskId);
        Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
        epic.deleteSubtaskId(subtaskId);
        subtasks.remove(subtaskId);
        defineEpicStatus(epic.getId());
    }

    @Override
    public ArrayList<Subtask> getListOfSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        for (int subtaskId : epic.getListOfSubtasksId()) {
            Subtask subtask = subtasks.get(subtaskId);
            listOfSubtasks.add(subtask);
        }
        return listOfSubtasks;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) (historyManager.getHistory());
    }

}

