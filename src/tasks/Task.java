package tasks;

import enums.Status;
import enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    private String name;
    private Integer id;
    private String description;
    private Status status;

    private Duration duration;
    private LocalDateTime startTime;


    public Task(int id, String name, String description, Status status,
                LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description) {
        this(0, name, description, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(15));
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Type getType() {
        return Type.TASK;
    }

    public Integer getEpicId() {
        return null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        String result = "Task{" +
                "name='" + name + '\'' +
                ", id=" + id;
        if (description != null) {
            result = result + ", description.length='" + description.length();
        } else {
            result = result + ", description=null";
        }
        return result + ", status=" + status +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                ", duration=" + duration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
