package manager;

import task.Epic;
import task.Subtask;
import task.Task;

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

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Task getTaskById(int uid) {
        if (tasks.containsKey(uid)) {
            return new Task(tasks.get(uid));
        }
        return null;
    }

    public Epic getEpicById(int uid) {
        if (epics.containsKey(uid)) {
            return new Epic(epics.get(uid));
        }
        return null;
    }

    public Subtask getSubtaskById(int uid) {
        if (subtasks.containsKey(uid)) {
            return new Subtask(subtasks.get(uid));
        }
        return null;
    }

    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = this.getEpicById(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(epic.getSubtasks());
    }

    public int createTask(Task task) {
        if (task != null) {
            int uid = getUidCounter();
            task.setUid(uid);
            tasks.put(uid, task);
            return uid;
        }
        return 0;
    }

    public int createEpic(Epic epic) {
        if (epic != null) {
            int uid = getUidCounter();
            epic.setUid(uid);
            epics.put(uid, epic);
            return uid;
        }
        return 0;
    }

    public int createSubtask(Subtask subtask) {
        if (subtask != null) {
            int epicUid = subtask.getEpicUid();
            Epic epic = epics.get(epicUid);
            if (epic != null) {
                int uid = getUidCounter();
                subtask.setUid(uid);
                subtasks.put(uid, subtask);
                epic.getSubtasks().add(subtask);
                epic.changeStatusDependingOnSubtasks();
                epics.put(epicUid, epic);
                return uid;
            }
        }
        return 0;
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getUid())) {
            tasks.put(task.getUid(), task);
        }
    }

    public void updateEpic(Epic epic) {
        int epicId = epic.getUid();
        if (epics.containsKey(epicId)) {
            Epic epic1 = epics.get(epicId);
            epic1.setDescription(epic.getDescription());
            epic1.setName(epic.getName());
            epics.put(epicId, epic1);
        }
    }

    public void updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getUid();
        if (subtasks.containsKey(subtaskId)) {
            Subtask oldSubtask = subtasks.get(subtaskId);
            subtasks.put(subtaskId, subtask);
            Epic epic = epics.get(oldSubtask.getEpicUid());
            //Epic epic = getEpicById(oldSubtask.getEpicUid());
            if (epic != null) {
                epic.getSubtasks().remove(oldSubtask);
                epic.getSubtasks().add(subtask);
                epic.changeStatusDependingOnSubtasks();
                updateEpic(epic);
            }
        }
    }

    public void deleteTaskById(int uid) {
        tasks.remove(uid);
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteEpicById(int uid) {
        if (epics.containsKey(uid)) {
            //Epic epic = getEpicById(uid);
            Epic epic = epics.get(uid);
            if (epic != null) {
                ArrayList<Subtask> epicSubtasks = epic.getSubtasks();
                for (Subtask subtask : epicSubtasks) {
                    int subtaskId = subtask.getUid();
                    subtasks.remove(subtaskId);
                }
                epics.remove(epic.getUid());
            }
        }
    }

    public void deleteAllEpics() {
        //deleteAllSubtasks();
        subtasks.clear();
        epics.clear();
    }

    public void deleteSubtaskById(int uid) {
        if (subtasks.containsKey(uid)) {
            //Subtask subtask = getSubtaskById(uid);
            Subtask subtask = subtasks.get(uid);
            if (subtask != null) {
                //Epic epic = getEpicById(subtask.getEpicUid());
                Epic epic = epics.get(subtask.getEpicUid());
                if (epic != null) {
                    subtasks.remove(uid);
                    epic.getSubtasks().remove(subtask);
                    epic.changeStatusDependingOnSubtasks();
                    epics.put(epic.getUid(), epic);
                    //updateEpic(epic);
                }
            }
        }
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.changeStatusDependingOnSubtasks();
        }
        subtasks.clear();
    }

}
