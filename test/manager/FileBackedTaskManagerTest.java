package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest extends TaskManagerAbstractTest<FileBackedTaskManager> {
    public File testFile;

    @Override
    public FileBackedTaskManager getTaskManager() {
        return new FileBackedTaskManager(testFile);
    }

    @BeforeEach
    public void beforeEach() {
        createNewFile();
        super.beforeEach();
    }

    private void createNewFile() {
        try {
            testFile = File.createTempFile("testFile", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void isTaskManagerEqualsLoadFromFileManager() {
        FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(testFile);

        HashMap<Integer, Task> loadedTasks = loadedTaskManager.tasks;
        assertEquals(taskManager.getTasks().size(), loadedTaskManager.getTasks().size());

        for (Map.Entry<Integer, Task> entry : taskManager.tasks.entrySet()) {
            Integer id = entry.getKey();
            Task task = entry.getValue();
            Task loadedTask = loadedTasks.get(id);
            assertEquals(task, loadedTask);
        }

        HashMap<Integer, Epic> loadedEpics = loadedTaskManager.epics;
        assertEquals(taskManager.getEpics().size(), loadedTaskManager.getEpics().size());

        for (Map.Entry<Integer, Epic> entry : taskManager.epics.entrySet()) {
            Integer id = entry.getKey();
            Epic epic = entry.getValue();
            Epic loadedEpic = loadedEpics.get(id);
            assertEquals(epic, loadedEpic);
        }

        HashMap<Integer, Subtask> loadedSubtasks = loadedTaskManager.subtasks;
        assertEquals(taskManager.getSubtasks().size(), loadedTaskManager.subtasks.size());

        for (Map.Entry<Integer, Subtask> entry : taskManager.subtasks.entrySet()) {
            Integer id = entry.getKey();
            Subtask subtask = entry.getValue();
            Subtask loadedSubtask = loadedSubtasks.get(id);
            assertEquals(subtask, loadedSubtask);
        }


    }

    @Test
    public void shouldCreateAndLoadEmptyFile() {
        createNewFile();
        FileBackedTaskManager taskManager1 = getTaskManager();
        taskManager1.save();

        assertTrue(taskManager1.getTasks().isEmpty());
        assertTrue(taskManager1.getSubtasks().isEmpty());
        assertTrue(taskManager1.getEpics().isEmpty());

        FileBackedTaskManager emptyTaskManager = FileBackedTaskManager.loadFromFile(testFile);

        assertTrue(emptyTaskManager.getTasks().isEmpty());
        assertTrue(emptyTaskManager.getSubtasks().isEmpty());
        assertTrue(emptyTaskManager.getEpics().isEmpty());

    }

    @Test
    public void shouldAddTask() {
        Task task = new Task("New Task", "New Task description");
        int taskId = taskManager.createTask(task);

        FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(testFile);

        HashMap<Integer, Task> loadedTasks = loadedTaskManager.tasks;
        Task loadedTask = loadedTasks.get(taskId);
        assertNotNull(loadedTask);
        assertEquals(taskManager.getTaskById(taskId), loadedTaskManager.getTaskById(taskId));
    }

    @Test
    public void shouldAddEpic() {
        Epic epic = new Epic("New Epic", "New epic description");
        int epicId = taskManager.createEpic(epic);

        FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(testFile);

        HashMap<Integer, Epic> loadedEpics = loadedTaskManager.epics;
        Epic loadedEpic = loadedEpics.get(epicId);

        assertNotNull(loadedEpic);
        assertEquals(taskManager.getEpicById(epicId), loadedTaskManager.getEpicById(epicId));
    }

    @Test
    public void shouldAddSubtask() {
        Epic epic = taskManager.getEpicById(epic1Id);
        Subtask subtask = new Subtask("New Subtask", "New Subtask for Epic1", epic);
        int subtaskId = taskManager.createSubtask(subtask);

        FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(testFile);

        HashMap<Integer, Subtask> loadedSubtasks = loadedTaskManager.subtasks;
        Subtask loadedSubtask = loadedSubtasks.get(subtaskId);
        assertNotNull(loadedSubtask);
        assertEquals(taskManager.getSubtaskById(subtaskId), loadedTaskManager.getSubtaskById(subtaskId));
    }

    @Test
    @Override
    public void shouldUpdateTask() {
        super.shouldUpdateTask();
        isTaskManagerEqualsLoadFromFileManager();
    }

    @Test
    @Override
    public void shouldUpdateEpic() {
        super.shouldUpdateEpic();
        isTaskManagerEqualsLoadFromFileManager();
    }

    @Test
    @Override
    public void shouldUpdateSubtask() {
        super.shouldUpdateSubtask();
        isTaskManagerEqualsLoadFromFileManager();
    }

    @Test
    @Override
    public void shouldDeleteTask() {
        super.shouldDeleteTask();
        isTaskManagerEqualsLoadFromFileManager();
    }

    @Test
    @Override
    public void shouldDeleteEpic() {
        super.shouldDeleteEpic();
        isTaskManagerEqualsLoadFromFileManager();
    }

    @Test
    @Override
    public void shouldDeleteSubtask() {
        super.shouldDeleteSubtask();
        isTaskManagerEqualsLoadFromFileManager();
    }

    @Test
    @Override
    public void shouldDeleteAllTasks() {
        super.shouldDeleteAllTasks();
        isTaskManagerEqualsLoadFromFileManager();
    }

    @Test
    @Override
    public void shouldDeleteAllEpics() {
        super.shouldDeleteAllEpics();
        isTaskManagerEqualsLoadFromFileManager();
    }

    @Test
    @Override
    public void shouldDeleteAllSubtasks() {
        super.shouldDeleteAllSubtasks();
        isTaskManagerEqualsLoadFromFileManager();
    }

    @Test
    @Override
    public void shouldChangeStatusWhereSubtaskStatusBecomeDone() {
        super.shouldChangeStatusWhereSubtaskStatusBecomeDone();
        isTaskManagerEqualsLoadFromFileManager();
    }

    @Test
    @Override
    public void shouldChangeStatusWhereSubtaskStatusBecomeInProgress() {
        super.shouldChangeStatusWhereSubtaskStatusBecomeInProgress();
        isTaskManagerEqualsLoadFromFileManager();
    }
}
