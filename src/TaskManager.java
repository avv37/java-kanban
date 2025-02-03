import Task.Epic;
import Task.Subtask;
import Task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int uidCounter = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int getUidCounter() {
        return uidCounter++;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public Task getTaskById(int uid) {
        return tasks.getOrDefault(uid, new Task());
    }

    public Epic getEpicById(int uid) {
        return epics.get(uid);
    }

    public Subtask getSubtaskById(int uid) {
        return subtasks.get(uid);
    }

    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasks();
    }

    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        return getSubtasksByEpic(epic);
    }


    public void createTask(Task task) {
        int uid = getUidCounter();
        task.setUid(uid);
        updateTask(task);
    }

    public void createEpic(Epic epic) {
        int uid = getUidCounter();
        epic.setUid(uid);
        updateEpic(epic);
    }

    public void createSubtask(Subtask subtask) {
        int uid = getUidCounter();
        subtask.setUid(uid);
        updateSubtask(subtask);
    }

    public void updateTask(Task task) {
        tasks.put(task.getUid(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getUid(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getUid(), subtask);
        Epic epic = getEpicById(subtask.getEpicUid());
        epic.changeStatusDependingOnSubtasks();
    }

    public void deleteTask(Task task) {
        int taskId = task.getUid();
        deleteTaskById(taskId);
    }

    public void deleteTaskById(int uid) {
        tasks.remove(uid);
    }

    public void deleteAllTasks() {
        tasks.clear();
    }


    public void deleteEpic(Epic epic) {
        ArrayList<Subtask> epicSubtasks = epic.getSubtasks();
        for (Subtask subtask : epicSubtasks) {
            int subtaskId = subtask.getUid();
            subtasks.remove(subtaskId);
        }
        epics.remove(epic.getUid());
    }

    public void deleteEpicById(int uid) {
        Epic epic = getEpicById(uid);
        deleteEpic(epic);
    }

    public void deleteAllEpics() {
        for (Integer epicId : epics.keySet()) {
            deleteEpicById(epicId);
        }
    }

    public void deleteSubtask(Subtask subtask) {
        Epic epic = getEpicById(subtask.getEpicUid());
        epic.deleteSubtaskFromEpic(subtask);
        subtasks.remove(subtask.getUid());
    }

    public void deleteSubtaskById(int uid) {
        Subtask subtask = getSubtaskById(uid);
        deleteSubtask(subtask);
    }

    public void deleteAllSubtasks() {
        ArrayList<Integer> allSubtaskIds = new ArrayList<>(subtasks.keySet());
        for (Integer subtaskId : allSubtaskIds) {
            deleteSubtaskById(subtaskId);
        }
    }

}
