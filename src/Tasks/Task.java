package Task;

import java.util.Objects;

public class Task {
    String name; //Название задачи
    private int id;
    String description; //Описание задачи
    Status status = Status.NEW; // сразу задать NEW? в конструкторе?

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
}
