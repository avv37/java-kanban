package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskTest {
    public TaskManager taskManager;
    public int task1Id;
    public Task task;

    @BeforeEach
    public void BeforeEach() {
        taskManager = Managers.getDefault();
        task = new Task("Task1", "First task description");
        task1Id = taskManager.createTask(task);
    }

    @Test
    public void shouldCreateTask() {
        Task savedTask = taskManager.getTaskById(task1Id);
        Assertions.assertNotNull(savedTask, "Задача не найдена.");
        Assertions.assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void isTasksEqualsWhenIdsEquals() {
        Task task1 = taskManager.getTaskById(task1Id);
        Assertions.assertEquals(task, task1);
    }

}