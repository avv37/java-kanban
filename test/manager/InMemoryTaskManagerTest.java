package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InMemoryTaskManagerTest {
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
    }

    @Test
    public void shouldDeleteAllTasks() {
        assertEquals(3, taskManager.getTasks().size());
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getTasks().size());
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
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(2, taskManager.getEpicById(epic1Id).getSubtasks().size());

        taskManager.deleteEpicById(epic1Id);

        assertEquals(0, taskManager.getEpics().size());
        assertNull(taskManager.getEpicById(epic1Id));
    }

    @Test
    public void shouldDeleteAllEpics() {
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(2, taskManager.getEpicById(epic1Id).getSubtasks().size());

        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getEpics().size());
        assertNull(taskManager.getEpicById(epic1Id));
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
        assertEquals(2, taskManager.getSubtasks().size());
        taskManager.deleteSubtaskById(subtask2Id);
        assertEquals(1, taskManager.getSubtasks().size());
    }

    @Test
    public void shouldDeleteAllSubtasks() {
        assertEquals(2, taskManager.getSubtasks().size());
        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getSubtasks().size());
    }
}