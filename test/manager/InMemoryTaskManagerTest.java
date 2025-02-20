package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

class InMemoryTaskManagerTest {
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
        Assertions.assertEquals("Task 1.1", savedTaskNew.getName());
        Assertions.assertEquals("First task description with change", savedTaskNew.getDescription());
        Assertions.assertEquals(Status.IN_PROGRESS, savedTaskNew.getStatus());
    }

    @Test
    public void shouldNotChangeTaskId() {
        Task savedTask = taskManager.getTaskById(task1Id);
        savedTask.setUid(111);
        taskManager.updateTask(savedTask);

        Task savedTaskNew = taskManager.getTaskById(task1Id);
        Assertions.assertEquals(savedTask.getUid(), savedTaskNew.getUid(), "ID не должен меняться.");
    }

    @Test
    public void shouldDeleteTask() {
        Assertions.assertEquals(3, taskManager.getTasks().size());
        taskManager.deleteTaskById(task2Id);
        Assertions.assertEquals(2, taskManager.getTasks().size());
    }

    @Test
    public void shouldDeleteAllTasks() {
        Assertions.assertEquals(3, taskManager.getTasks().size());
        taskManager.deleteAllTasks();
        Assertions.assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void shouldUpdateEpic() {
        Epic savedEpic = taskManager.getEpicById(epic1Id);
        savedEpic.setName("Epic1.1");
        savedEpic.setDescription("New First epic description");
        taskManager.updateEpic(savedEpic);

        Epic savedEpicNew = taskManager.getEpicById(epic1Id);
        Assertions.assertEquals("Epic1.1", savedEpicNew.getName());
        Assertions.assertEquals("New First epic description", savedEpicNew.getDescription());
    }

    @Test
    public void shouldNotChangeEpicStatus() {
        Epic savedEpic = taskManager.getEpicById(epic1Id);
        savedEpic.setStatus(Status.IN_PROGRESS);
        taskManager.updateEpic(savedEpic);

        Epic savedEpicNew = taskManager.getEpicById(epic1Id);
        Assertions.assertEquals(Status.NEW, savedEpicNew.getStatus());
    }

    @Test
    public void shouldNotChangeEpicId() {
        Epic savedEpic = taskManager.getEpicById(epic1Id);
        int Id = savedEpic.getUid();
        savedEpic.setUid(123);
        taskManager.updateEpic(savedEpic);

        Epic savedEpicNew = taskManager.getEpicById(epic1Id);
        Assertions.assertEquals(Id, savedEpicNew.getUid());
    }

    @Test
    public void shouldDeleteEpic() {
        Assertions.assertEquals(1, taskManager.getEpics().size());
        Assertions.assertEquals(2, taskManager.getEpicById(epic1Id).getSubtasks().size());

        taskManager.deleteEpicById(epic1Id);

        Assertions.assertEquals(0, taskManager.getEpics().size());
        Assertions.assertNull(taskManager.getEpicById(epic1Id));
    }

    @Test
    public void shouldDeleteAllEpics() {
        Assertions.assertEquals(1, taskManager.getEpics().size());
        Assertions.assertEquals(2, taskManager.getEpicById(epic1Id).getSubtasks().size());

        taskManager.deleteAllEpics();

        Assertions.assertEquals(0, taskManager.getEpics().size());
        Assertions.assertNull(taskManager.getEpicById(epic1Id));
    }

    @Test
    public void shouldChangeStatusWhereSubtaskStatusBecomeInProgress() {
        Subtask savedSubTask1 = taskManager.getSubtaskById(subtask1Id);
        savedSubTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(savedSubTask1);

        Epic savedEpic = taskManager.getEpicById(epic1Id);
        Status status = savedEpic.getStatus();

        Assertions.assertEquals(Status.IN_PROGRESS, status, "Статус не IN_PROGRESS");
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

        Assertions.assertEquals(Status.DONE, savedEpic.getStatus(), "Статус не DONE");
    }

    @Test
    public void shouldUpdateSubtask() {
        Subtask savedSubTask1 = taskManager.getSubtaskById(subtask1Id);
        savedSubTask1.setName("Subtask1.1");
        savedSubTask1.setDescription("Subtask1.1 for first Epic");
        savedSubTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(savedSubTask1);

        Subtask savedSubTaskNew = taskManager.getSubtaskById(subtask1Id);
        Assertions.assertEquals("Subtask1.1", savedSubTaskNew.getName());
        Assertions.assertEquals("Subtask1.1 for first Epic", savedSubTaskNew.getDescription());
        Assertions.assertEquals(Status.IN_PROGRESS, savedSubTaskNew.getStatus());
    }
    @Test
    public void shouldNotChangeSubtaskId() {
        Subtask savedSubTask1 = taskManager.getSubtaskById(subtask1Id);
        savedSubTask1.setUid(123);
        taskManager.updateSubtask(savedSubTask1);

        Subtask savedSubTaskNew = taskManager.getSubtaskById(subtask1Id);
        Assertions.assertEquals(savedSubTask1.getUid(), savedSubTaskNew.getUid(), "ID не должен меняться.");
    }

    @Test
    public void shouldDeleteSubtask() {
        Assertions.assertEquals(2, taskManager.getSubtasks().size());
        taskManager.deleteSubtaskById(subtask2Id);
        Assertions.assertEquals(1, taskManager.getSubtasks().size());
    }
    @Test
    public void shouldDeleteAllSubtasks() {
        Assertions.assertEquals(2, taskManager.getSubtasks().size());
        taskManager.deleteAllSubtasks();
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
    }
}