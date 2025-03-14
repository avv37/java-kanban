package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubtaskTest {
    public TaskManager taskManager;
    public Epic epic;
    public int epic1Id;
    public Subtask subtask1;
    public Subtask subtask2;
    public int subtask1Id;
    public int subtask2Id;

    @BeforeEach
    public void beforeEach() {
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
        assertNotNull(savedSubTask1, "Подзадача не найдена.");
        assertEquals(subtask1, savedSubTask1, "Подзадачи не совпадают.");

        final Subtask savedSubTask2 = taskManager.getSubtaskById(subtask2Id);
        assertNotEquals(savedSubTask2, savedSubTask1, "Разные подзадачи не должны совпадать.");
    }

    @Test
    public void isSubtasksEqualsWhenIdsEquals() {
        Subtask savedSubTask1 = taskManager.getSubtaskById(subtask1Id);
        assertEquals(subtask1, savedSubTask1);
    }

}