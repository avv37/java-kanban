package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class EpicTest {
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
        Epic savedEpic1 = taskManager.getEpicById(epic1Id);

        subtask1 = new Subtask("Subtask1", "Subtask1 for Epic1", savedEpic1);
        subtask1Id = taskManager.createSubtask(subtask1);

        subtask2 = new Subtask("Subtask2", "Subtask2 for Epic1", savedEpic1);
        subtask2Id = taskManager.createSubtask(subtask2);
    }

    @Test
    public void shouldCreateEpic() {
        Epic savedEpic = taskManager.getEpicById(epic1Id);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
    }

    @Test
    public void isEpicsEqualsWhenIdsEquals() {
        Epic epic1 = taskManager.getEpicById(epic1Id);

        assertEquals(epic, epic1);
    }

    @Test
    public void shouldRemoveSubtaskOnlyByManager() {
        Epic epic1 = taskManager.getEpicById(epic1Id);
        ArrayList<Subtask> subtasks = epic1.getSubtasks();
        subtasks.removeFirst();
        Epic epic2 = taskManager.getEpicById(epic1Id);

        taskManager.deleteSubtaskById(subtask1Id);
        Epic epic3 = taskManager.getEpicById(epic1Id);

        assertEquals(1, epic1.getSubtasks().size());
        assertEquals(2, epic2.getSubtasks().size());
        assertEquals(1, epic3.getSubtasks().size());
    }

}