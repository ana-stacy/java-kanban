package manager;

import exceptions.NotFoundException;
import exceptions.ValidationException;
import tasks.Epic;
import tasks.Task;
import tasks.Subtask;
import enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, Subtask> subtasks;
    protected HistoryManager historyManager;
    protected int idCounter = 0;

    protected TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
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

    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void clearListOfTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
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
            prioritizedTasks.remove(subtask);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearListOfSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
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

    private boolean isTimeIntersects(Task task1, Task task2) {
        if (task1.getId() == task2.getId()) {
            return false;
        }
        return task1.getStartTime().isBefore(task2.getEndTime()) && task1.getEndTime().isAfter(task2.getStartTime());
    }

    @Override
    public void createNewTask(Task task) {

        if (task.getStartTime() != null) {
            Task conflictingTask = getPrioritizedTasks().stream()
                    .filter(existingTask -> isTimeIntersects(existingTask, task))
                    .findFirst()
                    .orElse(null);

            if (conflictingTask != null) {
                throw new ValidationException("Пересечение по времени с задачей по id: " + conflictingTask.getId());
            }
        }

        task.setId(++idCounter);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void createNewEpic(Epic epic) {
        epic.setId(++idCounter);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createNewSubtask(Subtask subtask) {

        if (subtask.getStartTime() != null) { // добавлен комментарий в Pull request
            Task conflictingTask = getPrioritizedTasks().stream()
                    .filter(existingTask -> isTimeIntersects(existingTask, subtask))
                    .findFirst()
                    .orElse(null);

            if (conflictingTask != null) {
                throw new ValidationException("Пересечение по времени с задачей по id: " + conflictingTask.getId());
            }
        }

        subtask.setId(++idCounter);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(idCounter);
        defineEpicStatus(epic.getId());
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
            setEpicTime(epic.getId());
        }
    }

    @Override
    public void updateTask(Task task) {

        Task originalTask = tasks.get(task.getId());
        if (originalTask == null) {
            throw new NotFoundException("Задача не найдена");
        }

        if (task.getStartTime() != null) {
            Task conflictingTask = getPrioritizedTasks().stream()
                    .filter(existingTask -> isTimeIntersects(existingTask, task))
                    .findFirst()
                    .orElse(null);

            if (conflictingTask != null) {
                throw new ValidationException("Пересечение по времени с задачей по id: " + conflictingTask.getId());
            }
            prioritizedTasks.remove(originalTask);
            prioritizedTasks.add(task);
        }

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {

        Task originalEpic = epics.get(epic.getId());
        if (originalEpic == null) {
            throw new NotFoundException("Эпик не найден");
        }

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

    private void setEpicTime(int epicId) {
        LocalDateTime start = LocalDateTime.MAX;
        LocalDateTime end = LocalDateTime.MIN;
        Epic epic = epics.get(epicId);
        for (int subtaskId : epic.getListOfSubtasksId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStartTime().isBefore(start)) {
                start = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(end)) {
                end = subtask.getEndTime();
            }
        }
        epic.setStartTime(start);
        epic.setEndTime(end);
        epic.setDuration(Duration.between(start, end));
    }

    @Override
    public void updateSubtask(Subtask subtask) {

        Task originalSubtask = subtasks.get(subtask.getId());
        if (originalSubtask == null) {
            throw new NotFoundException("Подзадача не найдена");
        }

        if (subtask.getStartTime() != null) {
            Task conflictingTask = getPrioritizedTasks().stream()
                    .filter(existingTask -> isTimeIntersects(existingTask, subtask))
                    .findFirst()
                    .orElse(null);

            if (conflictingTask != null) {
                throw new ValidationException("Пересечение по времени с задачей по id: " + conflictingTask.getId());
            }

            prioritizedTasks.remove(originalSubtask);
            prioritizedTasks.add(subtask);
            setEpicTime(subtask.getEpicId());
        }

        subtasks.put(subtask.getId(), subtask);
        defineEpicStatus(subtask.getEpicId());
    }

    @Override
    public void deleteTaskById(int taskId) {
        historyManager.remove(taskId);
        prioritizedTasks.remove(tasks.get(taskId));
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
                prioritizedTasks.remove(subtasks.get(subtaskId));
            }
            epics.remove(epicId);
        }
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        historyManager.remove(subtaskId);
        prioritizedTasks.remove(subtasks.get(subtaskId));
        Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
        epic.deleteSubtaskId(subtaskId);
        subtasks.remove(subtaskId);
        defineEpicStatus(epic.getId());
        setEpicTime(epic.getId());
    }

    @Override
    public ArrayList<Subtask> getListOfSubtasksByEpic(int epicId) {
        return epics.get(epicId).getListOfSubtasksId().stream()
                .map(subtaskId -> subtasks.get(subtaskId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}

