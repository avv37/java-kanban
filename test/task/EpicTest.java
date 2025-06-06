package task;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
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

        subtask1 = new Subtask("Subtask1", "Subtask1 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 12, 0));
        subtask1Id = taskManager.createSubtask(subtask1);

        subtask2 = new Subtask("Subtask2", "Subtask2 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 16, 0));
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

    @Test
    public void shouldCalculateTimeAndDuration() {
        assertEquals(120, epic.duration.toMinutes());
        assertEquals(LocalDateTime.of(2025, 3, 10, 12, 0), epic.startTime);
        assertEquals(LocalDateTime.of(2025, 3, 10, 17, 0), epic.getEndTime());

        // вставил в середину
        Subtask subtask3 = new Subtask("Subtask3", "Subtask3 for Epic1", epic, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 14, 0));
        int subtask3Id = taskManager.createSubtask(subtask3);

        assertEquals(180, epic.duration.toMinutes());
        assertEquals(LocalDateTime.of(2025, 3, 10, 12, 0), epic.startTime);
        assertEquals(LocalDateTime.of(2025, 3, 10, 17, 0), epic.getEndTime());

        // вставил в начало
        Subtask subtask4 = new Subtask("Subtask4", "Subtask4 for Epic1", epic, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 10, 0));
        int subtask4Id = taskManager.createSubtask(subtask4);

        assertEquals(240, epic.duration.toMinutes());
        assertEquals(LocalDateTime.of(2025, 3, 10, 10, 0), epic.startTime);
        assertEquals(LocalDateTime.of(2025, 3, 10, 17, 0), epic.getEndTime());

        // вставил в конец
        Subtask subtask5 = new Subtask("Subtask5", "Subtask5 for Epic1", epic, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 17, 0));
        int subtask5Id = taskManager.createSubtask(subtask5);

        assertEquals(300, epic.duration.toMinutes());
        assertEquals(LocalDateTime.of(2025, 3, 10, 10, 0), epic.startTime);
        assertEquals(LocalDateTime.of(2025, 3, 10, 18, 0), epic.getEndTime());

        // изменил duration у сабтаска
        subtask5.duration = Duration.ofMinutes(90);
        taskManager.updateSubtask(subtask5);

        assertEquals(330, epic.duration.toMinutes());
        assertEquals(LocalDateTime.of(2025, 3, 10, 10, 0), epic.startTime);
        assertEquals(LocalDateTime.of(2025, 3, 10, 18, 30), epic.getEndTime());

        // изменил startTime у сабтаска в начале
        subtask4.startTime = LocalDateTime.of(2025, 3, 10, 10, 30);
        taskManager.updateSubtask(subtask4);

        assertEquals(330, epic.duration.toMinutes());
        assertEquals(LocalDateTime.of(2025, 3, 10, 10, 30), epic.startTime);
        assertEquals(LocalDateTime.of(2025, 3, 10, 18, 30), epic.getEndTime());

        // изменил startTime у сабтаска в конце
        subtask5.startTime = LocalDateTime.of(2025, 3, 10, 17, 30);
        taskManager.updateSubtask(subtask5);

        assertEquals(330, epic.duration.toMinutes());
        assertEquals(LocalDateTime.of(2025, 3, 10, 10, 30), epic.startTime);
        assertEquals(LocalDateTime.of(2025, 3, 10, 19, 0), epic.getEndTime());

    }

}