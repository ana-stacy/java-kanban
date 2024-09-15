package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtasksId;

    public Epic(String name, String description) {
        super(name, description);
        subtasksId = new ArrayList<>();
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
