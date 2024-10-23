package tasks;

import enums.Status;
import enums.Type;

public final class TaskConverter {

    public static String toString(Task task) {
        String taskToString;
        if (task.getType() == Type.TASK || task.getType() == Type.EPIC) {
            taskToString = task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                    + task.getDescription() + ",";
        } else {
            taskToString = task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + task.getEpicId();
        }
        return taskToString;
    }

    public static Task fromString(String value) {
        String[] split = value.split(",");
        Task task;

        int id = Integer.parseInt(split[0]);
        Type type = Type.valueOf(split[1]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];

        if (type == Type.TASK) {
            task = new Task(id, name, description, status);
        } else if (type == Type.EPIC) {
            task = new Epic(id, name, description, status);
        } else {
            int epicId = Integer.parseInt(split[5]);
            task = new Subtask(id, name, description, status, epicId);
        }
        return task;
    }
}
