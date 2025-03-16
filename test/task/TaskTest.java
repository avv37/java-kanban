package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskTest {
    public TaskManager taskManager;
    public int task1Id;
    public Task task;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        task = new Task("Task1", "First task description");
        task1Id = taskManager.createTask(task);
    }

    @Test
    public void shouldCreateTask() {
        Task savedTask = taskManager.getTaskById(task1Id);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void isTasksEqualsWhenIdsEquals() {
        Task task1 = taskManager.getTaskById(task1Id);
        assertEquals(task, task1);
    }

    @Test
    public void shouldChangeTaskOnlyNyManager() {
        Task task1 = taskManager.getTaskById(task1Id);
        task1.setName("New Name");
        task1.setStatus(Status.DONE);

        Task task2 = taskManager.getTaskById(task1Id);

        assertNotEquals(task1.getName(), task2.getName());
        assertNotEquals(task1.getStatus(), task2.getStatus());
    }

}