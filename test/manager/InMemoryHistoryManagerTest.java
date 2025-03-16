package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InMemoryHistoryManagerTest {

    public HistoryManager historyManager;
    public TaskManager taskManager;
    public int task1Id;
    public Task task1;
    public int task2Id;
    public Task task2;
    public int task3Id;
    public Task task3;
    public Epic epic1;
    public int epic1Id;
    public Subtask subtask1;
    public Subtask subtask2;
    public int subtask1Id;
    public int subtask2Id;

    @BeforeEach
    public void beforeEach() {
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
    public void isHistoryManagerStoresLastVersions() {
        Task task;
        task = taskManager.getTaskById(task1Id);
        task = taskManager.getTaskById(task2Id);
        task = taskManager.getTaskById(task3Id);
        task1.setName("Task1.1");
        task1.setDescription("First task description with change");
        task1.setStatus(Status.IN_PROGRESS);
        task = taskManager.getTaskById(task1Id);
        List<Task> taskHistory = taskManager.getHistory();

        assertEquals(task1Id, taskHistory.get(3).getUid());
        assertEquals("First task description with change", taskHistory.get(3).getDescription());
        assertNotEquals(task1Id, taskHistory.get(1).getUid());
    }

    @Test
    public void isHistoryManagerStoresOnlyOneTaskView() {
        Task task;
        task = taskManager.getTaskById(task1Id); // первый просмотр
        task = taskManager.getTaskById(task2Id);
        task = taskManager.getTaskById(task3Id);
        Subtask subtask;
        subtask = taskManager.getSubtaskById(subtask1Id); //должна остаться на первом месте
        task = taskManager.getTaskById(task1Id); // второй просмотр
        subtask = taskManager.getSubtaskById(subtask2Id);
        Epic epic = taskManager.getEpicById(epic1Id);
        task = taskManager.getTaskById(task1Id); // третий просмотр
        task = taskManager.getTaskById(task2Id);
        task = taskManager.getTaskById(task3Id);
        List<Task> taskHistory = taskManager.getHistory();
        int count = 0;
        for (Task t : taskHistory) {
            if (t.getUid() == task1Id) {
                count++;
            }
        }

        assertEquals(1, count);
        assertEquals(6, taskManager.getHistory().size());
        assertEquals(subtask1Id, taskHistory.getFirst().getUid());
    }

}