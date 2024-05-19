package Tasks;

import java.util.Objects;

public class Task {

    private String name;
    private int id;
    private String description;

    private Status status = Status.NEW;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getId() {
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
        int hash = 17;
        if (id != 0) {
            hash = hash + id;
        }
        return hash;
    }
}
