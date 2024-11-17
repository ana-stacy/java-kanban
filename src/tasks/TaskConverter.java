package tasks;

import enums.Status;
import enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;

public final class TaskConverter {

    public static String toString(Task task) {

        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + task.getEpicId() + ","
                    + task.getStartTime() + "," + task.getEndTime() + "," +  task.getDuration().toMinutes() + ",";
    }

    public static Task fromString(String value) {
        String[] split = value.split(",");
        Task task;

        int id = Integer.parseInt(split[0]);
        Type type = Type.valueOf(split[1]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        LocalDateTime startTime = LocalDateTime.parse(split[6]);
        long durationInMinutes = Long.parseLong(split[8]);
        Duration duration = Duration.ofMinutes(durationInMinutes);

        if (type == Type.TASK) {
            task = new Task(id, name, description, status, startTime, duration);
        } else if (type == Type.EPIC) {
            task = new Epic(id, name, description, status, startTime, duration);
        } else {
            int epicId = Integer.parseInt(split[5]);
            task = new Subtask(id, name, description, status, epicId, startTime, duration);
        }
        return task;
    }
}
