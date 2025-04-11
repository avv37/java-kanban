package manager;

import exception.SaveTaskException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class TaskManagerAbstractTest<T extends TaskManager> {
    public T taskManager;
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
    public Subtask subtask3;
    public int subtask1Id;
    public int subtask2Id;
    public int subtask3Id;

    abstract T getTaskManager();

    @BeforeEach
    public void beforeEach() {
        taskManager = getTaskManager();

        task1 = new Task("Task1", "First task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 14, 0));
        task1Id = taskManager.createTask(task1);
        task2 = new Task("Task2", "Second task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 12, 0));
        task2Id = taskManager.createTask(task2);
        task3 = new Task("Task3", "Third task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 10, 0));
        task3Id = taskManager.createTask(task3);

        epic1 = new Epic("Epic1", "First epic description");
        epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic1 = taskManager.getEpicById(epic1Id);

        subtask1 = new Subtask("Subtask1", "Subtask1 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 16, 0));
        subtask1Id = taskManager.createSubtask(subtask1);

        subtask2 = new Subtask("Subtask2", "Subtask2 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 18, 0));
        subtask2Id = taskManager.createSubtask(subtask2);

        subtask3 = new Subtask("Subtask3", "Subtask3 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 17, 0));
        subtask3Id = taskManager.createSubtask(subtask3);
    }

    @Test
    public void shouldUpdateTask() {
        Task savedTask = taskManager.getTaskById(task1Id);
        savedTask.setName("Task 1.1");
        savedTask.setDescription("First task description with change");
        savedTask.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(savedTask);

        Task savedTaskNew = taskManager.getTaskById(task1Id);
        assertEquals("Task 1.1", savedTaskNew.getName());
        assertEquals("First task description with change", savedTaskNew.getDescription());
        assertEquals(Status.IN_PROGRESS, savedTaskNew.getStatus());

        // изменилось в истории
        List<Task> history = taskManager.getHistory().stream()
                .filter(task -> task1Id == task.getUid())
                .toList();
        assertEquals(1, history.size());
        assertEquals("First task description with change", history.get(0).getDescription());

        // изменилось в упорядоченном списке
        List<Task> prioList = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task1Id == task.getUid())
                .toList();
        assertEquals(1, prioList.size());
        assertEquals("First task description with change", prioList.get(0).getDescription());
    }

    @Test
    public void shouldNotChangeTaskId() {
        Task savedTask = taskManager.getTaskById(task1Id);
        savedTask.setUid(111);
        taskManager.updateTask(savedTask);

        Task savedTaskNew = taskManager.getTaskById(task1Id);
        assertEquals(savedTask.getUid(), savedTaskNew.getUid(), "ID не должен меняться.");
    }

    @Test
    public void shouldDeleteTask() {
        assertEquals(3, taskManager.getTasks().size());
        taskManager.deleteTaskById(task2Id);
        assertEquals(2, taskManager.getTasks().size());

        // удален из истории
        List<Task> history = taskManager.getHistory().stream()
                .filter(task -> task2Id == task.getUid())
                .toList();
        assertTrue(history.isEmpty());

        // удален из упорядоченного списка
        List<Task> prioList = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task2Id == task.getUid())
                .toList();
        assertTrue(prioList.isEmpty());
    }

    @Test
    public void shouldDeleteAllTasks() {
        List<Task> oldTasks = taskManager.getTasks();
        assertEquals(3, taskManager.getTasks().size());
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getTasks().size());

        // удалены из истории
        List<Task> history = taskManager.getHistory().stream()
                .filter(oldTasks::contains)
                .toList();
        assertTrue(history.isEmpty());

        // удалены из упорядоченного списка
        List<Task> prioList = taskManager.getPrioritizedTasks().stream()
                .filter(oldTasks::contains)
                .toList();
        assertTrue(prioList.isEmpty());
    }

    @Test
    public void shouldUpdateEpic() {
        Epic savedEpic = taskManager.getEpicById(epic1Id);
        savedEpic.setName("Epic1.1");
        savedEpic.setDescription("New First epic description");
        taskManager.updateEpic(savedEpic);

        Epic savedEpicNew = taskManager.getEpicById(epic1Id);
        assertEquals("Epic1.1", savedEpicNew.getName());
        assertEquals("New First epic description", savedEpicNew.getDescription());

        // изменилось в истории
        List<Task> history = taskManager.getHistory().stream()
                .filter(task -> epic1Id == task.getUid())
                .toList();
        assertEquals(1, history.size());
        assertEquals("New First epic description", history.get(0).getDescription());
    }

    @Test
    public void shouldNotChangeEpicStatus() {
        Epic savedEpic = taskManager.getEpicById(epic1Id);
        savedEpic.setStatus(Status.IN_PROGRESS);
        taskManager.updateEpic(savedEpic);

        Epic savedEpicNew = taskManager.getEpicById(epic1Id);
        assertEquals(Status.NEW, savedEpicNew.getStatus());
    }

    @Test
    public void shouldNotChangeEpicId() {
        Epic savedEpic = taskManager.getEpicById(epic1Id);
        int Id = savedEpic.getUid();
        savedEpic.setUid(123);
        taskManager.updateEpic(savedEpic);

        Epic savedEpicNew = taskManager.getEpicById(epic1Id);
        assertEquals(Id, savedEpicNew.getUid());
    }

    @Test
    public void shouldDeleteEpic() {
        Task task;
        task = taskManager.getTaskById(task1Id);
        task = taskManager.getSubtaskById(subtask1Id);
        task = taskManager.getSubtaskById(subtask2Id);

        List<Subtask> oldSubTasks = taskManager.getEpicById(epic1Id).getSubtasks();
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(3, oldSubTasks.size());
        //
        List<Task> history = taskManager.getHistory().stream()
                .filter(oldSubTasks::contains)
                .toList();
        assertEquals(2, history.size());

        List<Task> prioList = taskManager.getPrioritizedTasks().stream()
                .filter(oldSubTasks::contains)
                .toList();
        assertEquals(3, prioList.size());

        taskManager.deleteEpicById(epic1Id);

        assertEquals(0, taskManager.getEpics().size());
        assertNull(taskManager.getEpicById(epic1Id));


        // удалились из истории
        history = taskManager.getHistory().stream()
                .filter(oldSubTasks::contains)
                .toList();
        assertTrue(history.isEmpty());
        // удалились из упорядоченного списка
        prioList = taskManager.getPrioritizedTasks().stream()
                .filter(oldSubTasks::contains)
                .toList();
        assertTrue(prioList.isEmpty());
    }

    @Test
    public void shouldDeleteAllEpics() {
        Epic epic2 = new Epic("Epic2", "2-nd epic description");
        int epic2Id = taskManager.createEpic(epic2);
        Epic savedEpic2 = taskManager.getEpicById(epic2Id);

        Subtask subtask4 = new Subtask("Subtask4", "Subtask for Epic2", savedEpic2,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 20, 0));
        int subtask4Id = taskManager.createSubtask(subtask4);

        Subtask subtask5 = new Subtask("Subtask5", "2-nd Subtask for Epic2", savedEpic2,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 19, 0));
        int subtask5Id = taskManager.createSubtask(subtask5);

        Subtask s = taskManager.getSubtaskById(subtask4Id);
        s = taskManager.getSubtaskById(subtask1Id);

        List<Subtask> oldSubTasks1 = taskManager.getEpicById(epic1Id).getSubtasks();
        List<Subtask> oldSubTasks2 = taskManager.getEpicById(epic2Id).getSubtasks();

        assertEquals(2, taskManager.getEpics().size());
        assertEquals(3, oldSubTasks1.size());
        assertEquals(2, oldSubTasks2.size());
        // было 2 субтаска в истории
        List<Task> history = taskManager.getHistory().stream()
                .filter(task -> oldSubTasks1.contains(task) || oldSubTasks2.contains(task))
                .toList();
        assertEquals(2, history.size());
        // было 5 субтасков в упорядоченном списке
        List<Task> prioList = taskManager.getPrioritizedTasks().stream()
                .filter(task -> oldSubTasks1.contains(task) || oldSubTasks2.contains(task))
                .toList();
        assertEquals(5, prioList.size());

        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getEpics().size());
        assertNull(taskManager.getEpicById(epic1Id));


        // удалились из истории
        history = taskManager.getHistory().stream()
                .filter(task -> oldSubTasks1.contains(task) || oldSubTasks2.contains(task))
                .toList();
        assertTrue(history.isEmpty());
        // удалились из упорядоченного списка
        prioList = taskManager.getPrioritizedTasks().stream()
                .filter(task -> oldSubTasks1.contains(task) || oldSubTasks2.contains(task))
                .toList();
        assertTrue(prioList.isEmpty());
    }

    @Test
    public void shouldChangeStatusWhereSubtaskStatusBecomeInProgress() {
        Subtask savedSubTask1 = taskManager.getSubtaskById(subtask1Id);
        savedSubTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(savedSubTask1);

        Epic savedEpic = taskManager.getEpicById(epic1Id);
        Status status = savedEpic.getStatus();

        assertEquals(Status.IN_PROGRESS, status, "Статус не IN_PROGRESS");
    }

    @Test
    public void shouldChangeStatusWhereSubtaskStatusBecomeDone() {
        Subtask savedSubTask1 = taskManager.getSubtaskById(subtask1Id);
        savedSubTask1.setStatus(Status.DONE);
        taskManager.updateSubtask(savedSubTask1);
        Subtask savedSubTask2 = taskManager.getSubtaskById(subtask2Id);
        savedSubTask2.setStatus(Status.DONE);
        taskManager.updateSubtask(savedSubTask2);
        Subtask savedSubTask3 = taskManager.getSubtaskById(subtask3Id);
        savedSubTask3.setStatus(Status.DONE);
        taskManager.updateSubtask(savedSubTask3);

        Epic savedEpic = taskManager.getEpicById(epic1Id);

        assertEquals(Status.DONE, savedEpic.getStatus(), "Статус не DONE");
    }

    @Test
    public void shouldUpdateSubtask() {
        Subtask savedSubTask1 = taskManager.getSubtaskById(subtask1Id);
        savedSubTask1.setName("Subtask1.1");
        savedSubTask1.setDescription("Subtask1.1 for first Epic");
        savedSubTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(savedSubTask1);

        Subtask savedSubTaskNew = taskManager.getSubtaskById(subtask1Id);
        assertEquals("Subtask1.1", savedSubTaskNew.getName());
        assertEquals("Subtask1.1 for first Epic", savedSubTaskNew.getDescription());
        assertEquals(Status.IN_PROGRESS, savedSubTaskNew.getStatus());

        List<Task> history = taskManager.getHistory().stream()
                .filter(task -> task.getUid() == subtask1Id)
                .toList();
        assertEquals(1, history.size());
        assertEquals("Subtask1.1", history.get(0).getName());
        assertEquals("Subtask1.1 for first Epic", history.get(0).getDescription());
        assertEquals(Status.IN_PROGRESS, history.get(0).getStatus());

        List<Task> prioList = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getUid() == subtask1Id)
                .toList();
        assertEquals(1, prioList.size());
        assertEquals("Subtask1.1", prioList.get(0).getName());
        assertEquals("Subtask1.1 for first Epic", prioList.get(0).getDescription());
        assertEquals(Status.IN_PROGRESS, prioList.get(0).getStatus());
    }

    @Test
    public void shouldNotChangeSubtaskId() {
        Subtask savedSubTask1 = taskManager.getSubtaskById(subtask1Id);
        savedSubTask1.setUid(123);
        taskManager.updateSubtask(savedSubTask1);

        Subtask savedSubTaskNew = taskManager.getSubtaskById(subtask1Id);
        assertEquals(savedSubTask1.getUid(), savedSubTaskNew.getUid(), "ID не должен меняться.");
    }

    @Test
    public void shouldDeleteSubtask() {
        Task t;
        t = taskManager.getTaskById(task1Id);
        t = taskManager.getSubtaskById(subtask1Id);
        t = taskManager.getSubtaskById(subtask2Id);

        assertEquals(3, taskManager.getSubtasks().size());
        // был в истории
        List<Task> history = taskManager.getHistory().stream()
                .filter(task -> task.getUid() == subtask2Id)
                .toList();
        assertEquals(subtask2Id, history.get(0).getUid());
        // был в упорядоченном списке
        List<Task> prioList = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getUid() == subtask2Id)
                .toList();
        assertEquals(subtask2Id, prioList.get(0).getUid());

        taskManager.deleteSubtaskById(subtask2Id);

        assertEquals(2, taskManager.getSubtasks().size());
        // удалился из истории
        history = taskManager.getHistory().stream()
                .filter(task -> task.getUid() == subtask2Id)
                .toList();
        assertTrue(history.isEmpty());
        // удалился из упорядоченного списка
        prioList = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getUid() == subtask2Id)
                .toList();
        assertTrue(prioList.isEmpty());
    }

    @Test
    public void shouldDeleteAllSubtasks() {
        Task t;
        t = taskManager.getTaskById(task1Id);
        t = taskManager.getSubtaskById(subtask1Id);
        t = taskManager.getSubtaskById(subtask2Id);

        List<Subtask> savedSubtasks = taskManager.getSubtasks();
        assertEquals(3, savedSubtasks.size());
        // было 2 субтаска в истории
        List<Task> history = taskManager.getHistory().stream()
                .filter(savedSubtasks::contains)
                .toList();
        assertEquals(2, history.size());
        // было 3 субтаска в сортированном списке
        List<Task> prioList = taskManager.getPrioritizedTasks().stream()
                .filter(savedSubtasks::contains)
                .toList();
        assertEquals(3, prioList.size());

        taskManager.deleteAllSubtasks();

        assertEquals(0, taskManager.getSubtasks().size());
        // удалились из истории
        history = taskManager.getHistory().stream()
                .filter(savedSubtasks::contains)
                .toList();
        assertTrue(history.isEmpty());
        // удалились из упорядоченного списка
        prioList = taskManager.getPrioritizedTasks().stream()
                .filter(savedSubtasks::contains)
                .toList();
        assertTrue(prioList.isEmpty());
    }

    @Test
    public void shouldThrowExceptionIfTasksIntersected() {
        // совпадает начало
        Task task4 = new Task("Task4", "Intersect description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 14, 0));
        assertThrows(SaveTaskException.class, () -> taskManager.createTask(task4));
        // начало попало в пересечение
        Task task5 = new Task("Task4", "Intersect description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 10, 30));
        assertThrows(SaveTaskException.class, () -> taskManager.createTask(task5));
        // конец попал в пересечение
        Task task6 = new Task("Task4", "Intersect description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 11, 30));
        assertThrows(SaveTaskException.class, () -> taskManager.createTask(task6));
    }

    @Test
    public void arePrioritizedTasksOrderedByTime() {
        List<Task> prioList = taskManager.getPrioritizedTasks();
        LocalDateTime time0 = LocalDateTime.MIN;
        for (Task task : prioList) {
            LocalDateTime time1 = task.getStartTime();
            assertTrue(time1.isAfter(time0));
            time0 = time1;
        }
    }
}