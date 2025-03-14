package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int uidCounter = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private int getUidCounter() {
        return uidCounter++;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTaskById(int uid) {
        if (tasks.containsKey(uid)) {
            Task task = new Task(tasks.get(uid));
            historyManager.add(task);
            return task;
        }
        return null;
    }

    @Override
    public Epic getEpicById(int uid) {
        if (epics.containsKey(uid)) {
            Epic epic = new Epic(epics.get(uid));
            historyManager.add(epic);
            return epic;
        }
        return null;
    }

    @Override
    public Subtask getSubtaskById(int uid) {
        if (subtasks.containsKey(uid)) {
            Subtask subtask = new Subtask(subtasks.get(uid));
            historyManager.add(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = new Epic(epics.get(epicId));
            return new ArrayList<>(epic.getSubtasks());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public int createTask(Task task) {
        if (task != null) {
            int uid = getUidCounter();
            task.setUid(uid);
            tasks.put(uid, task);
            return uid;
        }
        return 0;
    }

    @Override
    public int createEpic(Epic epic) {
        if (epic != null) {
            int uid = getUidCounter();
            epic.setUid(uid);
            epics.put(uid, epic);
            return uid;
        }
        return 0;
    }

    @Override
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
                return uid;
            }
        }
        return 0;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getUid())) {
            tasks.put(task.getUid(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int epicId = epic.getUid();
        if (epics.containsKey(epicId)) {
            Epic epic1 = epics.get(epicId);
            epic1.setDescription(epic.getDescription());
            epic1.setName(epic.getName());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getUid();
        if (subtasks.containsKey(subtaskId)) {
            Subtask oldSubtask = subtasks.get(subtaskId);
            subtasks.put(subtaskId, subtask);
            Epic epic = epics.get(oldSubtask.getEpicUid());
            if (epic != null) {
                epic.getSubtasks().remove(oldSubtask);
                epic.getSubtasks().add(subtask);
                epic.changeStatusDependingOnSubtasks();
            }
        }
    }

    @Override
    public void deleteTaskById(int uid) {
        tasks.remove(uid);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpicById(int uid) {
        if (epics.containsKey(uid)) {
            Epic epic = epics.get(uid);
            if (epic != null) {
                ArrayList<Subtask> epicSubtasks = epic.getSubtasks();
                for (Subtask subtask : epicSubtasks) {
                    subtasks.remove(subtask.getUid());
                }
                epics.remove(epic.getUid());
            }
        }
    }

    @Override
    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteSubtaskById(int uid) {
        if (subtasks.containsKey(uid)) {
            Subtask subtask = subtasks.get(uid);
            if (subtask != null) {
                Epic epic = epics.get(subtask.getEpicUid());
                if (epic != null) {
                    subtasks.remove(uid);
                    epic.getSubtasks().remove(subtask);
                    epic.changeStatusDependingOnSubtasks();
                }
            }
        }
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.changeStatusDependingOnSubtasks();
        }
        subtasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }
}
