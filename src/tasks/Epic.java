package tasks;

import enums.Status;
import enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasksId;
    private LocalDateTime endTime;

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status, null, Duration.ZERO);
        this.endTime = null;
        this.subtasksId = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(0, name, description, Status.NEW, null, Duration.ZERO);
        this.endTime = null;
        this.subtasksId = new ArrayList<>();
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void addSubtaskId(int id) {
        subtasksId.add(id);
    }

    public void deleteSubtaskId(int id) {
        subtasksId.remove((Integer) id);
    }

    public ArrayList<Integer> getListOfSubtasksId() {
        return subtasksId;
    }

    public boolean isListOfSubtasksEmpty() {
        return subtasksId.isEmpty();
    }

    public void clearListOfSubtasksByEpic() {
        if (!isListOfSubtasksEmpty()) {
            subtasksId.clear();
        }
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'';
        if (getDescription() != null) {
            result = result + ", description.length='" + getDescription().length();
        } else {
            result = result + ", description=null";
        }
        if (subtasksId == null || subtasksId.isEmpty()) {
            result = result + ", subtasksId=null";
        } else {
            result = result + ", subtasksId=" + getListOfSubtasksId();
        }
        result = result + ", status=" + getStatus();
        if (subtasksId != null && !subtasksId.isEmpty()) {
            result = result +
                    ", startTime=" + getStartTime() +
                    ", endTime=" + getEndTime() +
                    ", duration=" + getDuration();
        }
        return result + '}';
    }

}
