package taskManagers;

import tasks.Task;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> memoryKeeper;

    public InMemoryHistoryManager() {
        memoryKeeper = new ArrayList<>(10);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(memoryKeeper);
    }

    @Override
    public void add(Task task) {
        if (memoryKeeper.size() == 10) {
            memoryKeeper.remove(0);
        }
        memoryKeeper.add(task);
    }

}
