package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int HISTORY_LENGTH = 10;
    private final ArrayList<Task> browsingHistory = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(browsingHistory);
    }

    /* Просмотренная задача может быть потом удалена,
       поэтому в истории нужно хранить задачу, а не её id.
     */
    @Override
    public void add(Task task) {
        if (task == null) return;
        if (browsingHistory.size() == HISTORY_LENGTH) {
            browsingHistory.removeFirst();
        }
        browsingHistory.add(new Task(task));
    }
}
