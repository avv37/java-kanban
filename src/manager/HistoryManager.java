package manager;

import task.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    public List<Task> getHistory();
}
