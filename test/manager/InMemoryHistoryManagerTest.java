package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.List;

class InMemoryHistoryManagerTest {

    public static HistoryManager historyManager;
    public static TaskManager taskManager;
    public static int task1Id;
    public static Task task1;
    public static int task2Id;
    public static Task task2;
    public static int task3Id;
    public static Task task3;
    public static Epic epic1;
    public static int epic1Id;
    public static Subtask subtask1;
    public static Subtask subtask2;
    public static int subtask1Id;
    public static int subtask2Id;

    @BeforeEach
    public void BeforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();

        task1 = new Task("Task1", "First task description");
        task1Id = taskManager.createTask(task1);
        task2 = new Task("Task2", "Second task description");
        task2Id = taskManager.createTask(task2);
        task3 = new Task("Task3", "Third task description");
        task3Id = taskManager.createTask(task3);

        epic1 = new Epic("Epic1", "First epic description");
        epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic1 = taskManager.getEpicById(epic1Id);

        subtask1 = new Subtask("Subtask1", "Subtask1 for Epic1", savedEpic1);
        subtask1Id = taskManager.createSubtask(subtask1);

        subtask2 = new Subtask("Subtask2", "Subtask2 for Epic1", savedEpic1);
        subtask2Id = taskManager.createSubtask(subtask2);
    }

    @Test
    public void isHistoryManagerStoresDifferentVersions() {
        Task task;
        task = taskManager.getTaskById(task1Id);
        task = taskManager.getTaskById(task2Id);
        task = taskManager.getTaskById(task3Id);
        task1.setName("Task1.1");
        task1.setDescription("First task description with change");
        task1.setStatus(Status.IN_PROGRESS);
        task = taskManager.getTaskById(task1Id);
        List<Task> taskHistory = taskManager.getHistory();
        Task t1 = taskHistory.get(1);
        Task t2 = taskHistory.get(4);
        Assertions.assertEquals(t1.getUid(), t2.getUid());
        Assertions.assertNotEquals(t1, t2);
    }

    @Test
    public void isHistoryManagerStoresLast10Browsings() {
        Task task;
        task = taskManager.getTaskById(task1Id);
        task = taskManager.getTaskById(task2Id);
        task = taskManager.getTaskById(task3Id);
        Subtask subtask;
        subtask = taskManager.getSubtaskById(subtask1Id);
        subtask = taskManager.getSubtaskById(subtask2Id);
        Epic epic = taskManager.getEpicById(epic1Id);
        task = taskManager.getTaskById(task1Id);
        task = taskManager.getTaskById(task2Id);
        task = taskManager.getTaskById(task3Id);

        Assertions.assertEquals(10, taskManager.getHistory().size());
        Task t1 = taskManager.getHistory().getFirst();
        Assertions.assertEquals(epic1.getUid(), t1.getUid());
        Assertions.assertEquals(epic1.getStatus(), t1.getStatus());
        Assertions.assertEquals(epic1.getName(), t1.getName());
        Assertions.assertEquals(epic1.getDescription(), t1.getDescription());

        task = taskManager.getTaskById(task1Id);
        Assertions.assertEquals(10, taskManager.getHistory().size());
        t1 = taskManager.getHistory().getFirst();
        Assertions.assertEquals(task1, t1);

        task = taskManager.getTaskById(task2Id);
        Assertions.assertEquals(10, taskManager.getHistory().size());
        t1 = taskManager.getHistory().getFirst();
        Assertions.assertEquals(task2, t1);
    }

}