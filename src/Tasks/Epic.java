package Task;

import java.util.ArrayList;
import java.util.Arrays;

public class Epic extends Task {

    private final ArrayList<Integer> subtasksId;

    public Epic (String name, String description) {
        super(name, description);
        subtasksId = new ArrayList<>();
    }

    public void addSubtaskId (int id) {
        subtasksId.add(id);
    }

    ArrayList<Integer> getListOfSubtasksId() {
        return subtasksId;
    }

    boolean isListOfSubtasksEmpty() {
        return subtasksId.isEmpty();
    }


    @Override
    public String toString() {
        String result = "Epic{" +
                "id=" + getId() +
                ", name='" + name + '\'';
        if (description != null) {
            result = result + ", description.length='" + description.length();
        } else {
            result = result + ", description=null";
        }
        if (subtasksId.isEmpty()) {
            result =  result + ", subtasksId=null";
        } else {
            result =  result + ", subtasksId=" + getListOfSubtasksId();
        }
        return result +
                ", status=" + status +
                '}';
    }
}
