package tasks;

import enums.Status;
import enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String name, String description, Status status, int epicId,
                   LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId) {
        this(0, name, description, Status.NEW, epicId, LocalDateTime.now(), Duration.ofMinutes(15));
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        String result =  "Subtask{" +
                "epicId=" + epicId +
                ", id=" + getId() +
                ", name='" + getName() + '\'';
        if (getDescription() != null) {
            result = result + ", description.length='" + getDescription().length();
        } else {
            result = result + ", description=null";
        }
        return result + ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                ", duration=" + getDuration() +
                '}';
    }

}
