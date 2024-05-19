package Tasks;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
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
                '}';
    }
}
