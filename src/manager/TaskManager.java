package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    Task getTaskById(int uid);

    Epic getEpicById(int uid);

    Subtask getSubtaskById(int uid);

    ArrayList<Subtask> getSubtasksByEpicId(int epicId);

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(int uid);

    void deleteAllTasks();

    void deleteEpicById(int uid);

    void deleteAllEpics();

    void deleteSubtaskById(int uid);

    void deleteAllSubtasks();

    public List<Task> getHistory();

}
