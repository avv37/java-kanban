package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class EpicTest {
    public TaskManager taskManager;
    public Epic epic;
    public int epic1Id;
    public Subtask subtask1;
    public Subtask subtask2;
    public int subtask1Id;
    public int subtask2Id;


    @BeforeEach
    public void BeforeEach() {
        taskManager = Managers.getDefault();
        epic = new Epic("Epic1", "First epic description");
        epic1Id = taskManager.createEpic(epic);
        Epic savedEpic1 = taskManager.getEpicById(epic1Id);

        subtask1 = new Subtask("Subtask1", "Subtask1 for Epic1", savedEpic1);
        subtask1Id = taskManager.createSubtask(subtask1);

        subtask2 = new Subtask("Subtask2", "Subtask2 for Epic1", savedEpic1);
        subtask2Id = taskManager.createSubtask(subtask2);
    }

    @Test
    public void shouldCreateEpic() {
        Epic savedEpic = taskManager.getEpicById(epic1Id);

        Assertions.assertNotNull(savedEpic, "Эпик не найден.");
        Assertions.assertEquals(epic, savedEpic, "Эпики не совпадают.");
    }

    @Test
    public void isEpicsEqualsWhenIdsEquals() {
        Epic epic1 = taskManager.getEpicById(epic1Id);
        Assertions.assertEquals(epic, epic1);
    }

}