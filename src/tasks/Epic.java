package tasks;

import enums.Status;
import enums.Type;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasksId;

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(String name, String description) {
        this(0, name, description, Status.NEW);
    }

    @Override
    public Type getType() {
        return Type.EPIC;
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
        if (subtasksId.isEmpty()) {
            result =  result + ", subtasksId=null";
        } else {
            result =  result + ", subtasksId=" + getListOfSubtasksId();
        }
        return result +
                ", status=" + getStatus() +
                '}';
    }

}
