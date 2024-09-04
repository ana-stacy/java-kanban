import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import taskManagers.HistoryManager;
import taskManagers.Managers;
import taskManagers.TaskManager;

public class ManagersTest {

    @Test
    public void getDefaultTest() {
        TaskManager taskManager = Managers.getDefault();
        Assertions.assertNotNull(taskManager);
    }

    @Test
    public void getDefaultHistoryTest() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(historyManager);
    }

}
