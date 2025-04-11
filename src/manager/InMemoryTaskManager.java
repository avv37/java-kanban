package manager;

import exception.SaveTaskException;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected int uidCounter = 1;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(new Comparator<Task>() {
        @Override
        public int compare(Task o1, Task o2) {
            return (o1.getStartTime().isBefore(o2.getStartTime()) ? -1 :
                    o1.getStartTime().equals(o2.getStartTime()) ? 0 :
                            o1.getUid() == o2.getUid() ? 0 : 1);
        }
    });

    protected int getUidCounter() {
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
        if (task != null && canAddTaskWithoutIntersect(task)) {
            int uid = getUidCounter();
            task.setUid(uid);
            tasks.put(uid, task);
            putToPrioritizedTasks(task, null);
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
                if (canAddTaskWithoutIntersect(subtask)) {
                    int uid = getUidCounter();
                    subtask.setUid(uid);
                    subtasks.put(uid, subtask);
                    epic.getSubtasks().add(subtask);
                    epic.changeStatusDependingOnSubtasks();
                    epic.calculateStartTimeDurationEndTime();
                    putToPrioritizedTasks(subtask, null);
                    return uid;
                }
            }
        }
        return 0;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getUid())) {
            Task oldTask = tasks.get(task.getUid());
            if (canAddTaskWithoutIntersect(task)) {
                tasks.put(task.getUid(), task);
                putToPrioritizedTasks(task, oldTask);
            }
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
            if (canAddTaskWithoutIntersect(subtask)) {
                subtasks.put(subtaskId, subtask);
                Epic epic = epics.get(oldSubtask.getEpicUid());
                if (epic != null) {
                    epic.getSubtasks().remove(oldSubtask);
                    epic.getSubtasks().add(subtask);
                    epic.changeStatusDependingOnSubtasks();
                    epic.calculateStartTimeDurationEndTime();
                }
                putToPrioritizedTasks(subtask, oldSubtask);
            }
        }
    }

    @Override
    public void deleteTaskById(int uid) {
        historyManager.remove(uid);
        prioritizedTasksRemove(tasks.get(uid));
        tasks.remove(uid);
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasksRemove(tasks.get(id));
        }
        tasks.clear();
    }

    @Override
    public void deleteEpicById(int uid) {
        if (epics.containsKey(uid)) {
            Epic epic = epics.get(uid);
            if (epic != null) {
                for (Subtask subtask : epic.getSubtasks()) {
                    historyManager.remove(subtask.getUid());
                    prioritizedTasks.remove(subtask);
                    subtasks.remove(subtask.getUid());
                }
                historyManager.remove(uid);
                epics.remove(uid);
            }
        }
    }

    @Override
    public void deleteAllEpics() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        }
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
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
                    historyManager.remove(uid);
                    prioritizedTasks.remove(subtask);
                    subtasks.remove(uid);
                    epic.getSubtasks().remove(subtask);
                    epic.changeStatusDependingOnSubtasks();
                    epic.calculateStartTimeDurationEndTime();
                }
            }
        }
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        }
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.changeStatusDependingOnSubtasks();
            epic.calculateStartTimeDurationEndTime();
        }
        subtasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean canAddTaskWithoutIntersect(Task newTask) {
        if (newTask.getStartTime() == null) {
            return true;
        }
        String message = (newTask.getUid() == 0) ? "Новая задача " : ("Задача " + newTask.getUid());
        if (!prioritizedTasks.isEmpty()) {
            prioritizedTasks.stream()
                    .filter(task1 -> task1.getUid() != newTask.getUid())
                    .filter(task1 -> areTasksIntersected(newTask, task1))
                    .findFirst()
                    .ifPresentOrElse(
                            task1 -> {
                                throw new SaveTaskException(message
                                        + " пересекается по времени с существующей задачей " + task1.getUid());
                            },
                            () -> {
                            }
                    );
        }
        return true;
    }

    protected void putToPrioritizedTasks(Task newTask, Task oldTask) {
        if (oldTask == null && newTask.getStartTime() == null) {
            return;
        }
        if (oldTask != null && oldTask.getStartTime() != null) {
            prioritizedTasks.remove(oldTask); // Старая версия задачи была в отсортированном списке - удаляем
        }
        if (newTask.getStartTime() == null) {
            return; // Задача с пустым временем - не включаем
        }
        prioritizedTasks.add(newTask);
    }


    private boolean areTasksIntersected(Task task1, Task task2) {
        if (task1.getStartTime().isEqual(task2.getStartTime()) || task1.getEndTime().isEqual(task2.getEndTime())) {
            return true;
        }
        if (task1.getStartTime().isAfter(task2.getStartTime()) && task1.getStartTime().isBefore(task2.getEndTime())) {
            return true;
        }
        return task1.getEndTime().isAfter(task2.getStartTime()) && task1.getEndTime().isBefore(task2.getEndTime());
    }

    private void prioritizedTasksRemove(Task task) {
        if (task == null) return;
        prioritizedTasks.remove(task);
    }


}
