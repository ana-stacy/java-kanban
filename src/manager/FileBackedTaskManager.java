package manager;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Task;
import tasks.Subtask;
import enums.Type;
import tasks.TaskConverter;

import java.io.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public void save() throws ManagerSaveException {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file))) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : getListOfTasks()) {
                fileWriter.write(TaskConverter.toString(task) + "\n");
            }
            for (Epic epic : getListOfEpics()) {
                fileWriter.write(TaskConverter.toString(epic) + "\n");
            }
            for (Subtask subtask : getListOfSubtasks()) {
                fileWriter.write(TaskConverter.toString(subtask) + "\n");
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка в файле: " + file.getAbsolutePath(), exception);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, UTF_8))) {
            fileReader.readLine();
            int maxId = 0;
            while (fileReader.ready()) {
                String value = fileReader.readLine();
                Task task = TaskConverter.fromString(value);
                switch (task.getType()) {
                    case Type.TASK:
                        fileBackedTaskManager.tasks.put(task.getId(), task);
                        break;
                    case Type.EPIC:
                        fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                        break;
                    case Type.SUBTASK:
                        fileBackedTaskManager.subtasks.put(task.getId(), (Subtask) task);
                        int epicId = task.getEpicId();
                        Epic epic = fileBackedTaskManager.epics.get(epicId);
                        epic.addSubtaskId(task.getId());
                        break;
                }
                if (task.getId() > maxId) {
                    maxId = task.getId();
                }
            }
            fileBackedTaskManager.idCounter = maxId;
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка в файле: " + file.getAbsolutePath(), exception);
        }
        return fileBackedTaskManager;
    }

    @Override
    public void clearListOfTasks() {
        super.clearListOfTasks();
        save();
    }

    @Override
    public void clearListOfEpics() {
        super.clearListOfEpics();
        save();
    }

    @Override
    public void clearListOfSubtasks() {
        super.clearListOfSubtasks();
        save();
    }

    @Override
    public void createNewTask(Task task) {
        super.createNewTask(task);
        save();
    }

    @Override
    public void createNewEpic(Epic epic) {
        super.createNewEpic(epic);
        save();
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        super.createNewSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        super.deleteSubtaskById(subtaskId);
        save();
    }


}
