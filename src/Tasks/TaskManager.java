package Tasks;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, Subtask> subtasks;

    int idCounter = 0;

    public TaskManager () {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    // a. Получение списка всех задач.
    public ArrayList<Task> getListOfTasks() {
        ArrayList<Task> listOfTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            listOfTasks.add(task);
        }
        return listOfTasks;
    }

    public ArrayList<Epic> getListOfEpics() {
        ArrayList<Epic> listOfEpics = new ArrayList<>();
        for (Epic epic : epics.values()) {
            listOfEpics.add(epic);
        }
        return listOfEpics;
    }

    public ArrayList<Subtask> getListOfSubtasks() {
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            listOfSubtasks.add(subtask);
        }
        return listOfSubtasks;
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
        epics.clear();
    }

    // c. Получение по идентификатору.
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
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
    }

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask (Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic (int id) {
        Epic epic = epics.get(id);
        epic.status = defineEpicStatus(epic.status, getListOfSubtasksByEpic(id));
    }

    private Status defineEpicStatus(Status currentStatus, ArrayList<Subtask> listOfSubtasksByEpic) {
        ArrayList<Status> statuses = checkUniqueSubtasksStatuses(listOfSubtasksByEpic);
        if (statuses.contains(Status.IN_PROGRESS)) {
            return Status.IN_PROGRESS;
        } else if (statuses.size() == 1 && statuses.contains(Status.DONE)) {
            return Status.DONE;
        } else {
            return currentStatus;
        }
    }

    private ArrayList<Status> checkUniqueSubtasksStatuses(ArrayList<Subtask> listOfSubtasksByEpic) {
        ArrayList<Status> statuses = new ArrayList<>();
        for (Subtask subtask : listOfSubtasksByEpic) {
            Status status = subtask.status;
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
    public void deleteTaskById (int id) {
        tasks.remove(id);
    }

    public void deleteEpicById (int id) { // здесь нужно if - если есть подзадачи, то очистить, если нет, то просто удалить эпик?
        Epic epic = epics.get(id);
        if (!epic.isListOfSubtasksEmpty()) {
            for (int subtaskId : epic.getListOfSubtasksId()) {
                deleteSubtaskById(subtaskId);
            }
        }
        epics.remove(id);
    }

    public void deleteSubtaskById (int id) {
        subtasks.remove(id);
    }

    // Дополнительные методы:
    //a. Получение списка всех подзадач определённого эпика.
    ArrayList<Subtask> getListOfSubtasksByEpic (int id) {
        Epic epic = epics.get(id);
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        for (int subtaskId : epic.getListOfSubtasksId()) {
            Subtask subtask = subtasks.get(subtaskId);
            listOfSubtasks.add(subtask);
        }
        return listOfSubtasks;
    }
}

