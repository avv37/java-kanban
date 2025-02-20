package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubtaskTest {
    public static TaskManager taskManager;
    public static Epic epic;
    public static int epic1Id;
    public static Subtask subtask1;
    public static Subtask subtask2;
    public static int subtask1Id;
    public static int subtask2Id;
    @BeforeEach
    public void BeforeEach() {
        taskManager = Managers.getDefault();
        epic = new Epic("Epic1", "First epic description");
        epic1Id = taskManager.createEpic(epic);
        subtask1 = new Subtask("Subtask1", "First Subtask for first Epic", epic);
        subtask1Id = taskManager.createSubtask(subtask1);
        subtask2 = new Subtask("Subtask2", "Second Subtask for first Epic", epic);
        subtask2Id = taskManager.createSubtask(subtask2);
    }
    @Test
    public void shouldCreateSubtask() {
        Subtask savedSubTask1 = taskManager.getSubtaskById(subtask1Id);
        Assertions.assertNotNull(savedSubTask1, "Подзадача не найдена.");
        Assertions.assertEquals(subtask1, savedSubTask1, "Подзадачи не совпадают.");

        final Subtask savedSubTask2 = taskManager.getSubtaskById(subtask2Id);
        Assertions.assertNotEquals(savedSubTask2, savedSubTask1, "Разные подзадачи не должны совпадать.");
    }

    @Test
    public void isSubtasksEqualsWhenIdsEquals() {
        Subtask savedSubTask1 = taskManager.getSubtaskById(subtask1Id);
        Assertions.assertEquals(subtask1, savedSubTask1);
    }

}