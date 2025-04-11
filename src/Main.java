import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {


    public static File testFile = new File(System.getProperty("user.home"), "backedTasks.csv");
    public static TaskManager taskManager = Managers.getDefaultFileBacked(testFile);

    public static void main(String[] args) {
        System.out.println("Поехали!");
        System.out.println("Создание задач, эпиков и подзадач");
        Task task = new Task("Task 1", "First task description", Duration.ofMinutes(100), null);
        int task1Id = taskManager.createTask(task);
        task = new Task("Task 2", "Second task description", Duration.ofMinutes(120),
                LocalDateTime.of(2025, 3, 11, 13, 0));
        int task2Id = taskManager.createTask(task);
        task = new Task("Task 3", "Third task description", Duration.ofMinutes(120),
                LocalDateTime.of(2025, 3, 11, 10, 30));
        int task3Id = taskManager.createTask(task);
        System.out.println("task3Id = " + task3Id);
        Epic epic = new Epic("Epic 1", "First epic description");
        int epic1Id = taskManager.createEpic(epic);
        System.out.println("epic1Id = " + epic1Id);

        Subtask subtask = new Subtask("Subtask 1", "First Subtask for first Epic", epic,
                Duration.ofMinutes(150), LocalDateTime.of(2025, 3, 12, 11, 30));
        int subtask1Id = taskManager.createSubtask(subtask);
        System.out.println("subtask1Id = " + subtask1Id);

        subtask = new Subtask("Subtask 2", "Second Subtask for first Epic", epic,
                Duration.ofMinutes(300), LocalDateTime.of(2025, 3, 12, 15, 30));
        int subtask2Id = taskManager.createSubtask(subtask);
        System.out.println("subtask2Id = " + subtask2Id);

        subtask = new Subtask("Subtask 3", "3-d Subtask for first Epic", epic,
                Duration.ofMinutes(200), LocalDateTime.of(2025, 3, 13, 15, 30));
        int subtask3Id = taskManager.createSubtask(subtask);
        System.out.println("subtask3Id = " + subtask3Id);

        epic = new Epic("Epic 2", "Second epic description");
        int epic2Id = taskManager.createEpic(epic);
        System.out.println("epic2Id = " + epic2Id);

        subtask = new Subtask("Subtask 1 (2)", "First Subtask for second Epic", epic,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 12, 10, 30));
        int subtask3id = taskManager.createSubtask(subtask);
        System.out.println("subtask3id = " + subtask3id);

        System.out.println(taskManager.getPrioritizedTasks());
        System.out.println("---------------------");

        taskManager.deleteTaskById(10);

        System.out.println("---------------------");
        System.out.println("Список всех задач task   taskManager.getTasks()");
        System.out.println(taskManager.getTasks());
        System.out.println("Список всех эпиков epic   taskManager.getEpics()");
        System.out.println(taskManager.getEpics());
        System.out.println("Список всех подзадач subtask   taskManager.getSubtasks()");
        System.out.println(taskManager.getSubtasks());
        System.out.println("---------------------");


        System.out.println();
        System.out.println("просмотр task1");
        task = taskManager.getTaskById(task1Id);
        System.out.println(taskManager.getHistory());
        System.out.println("---------------------");
        task.setName("Task 1.1");
        task.setDescription("First task description with change");
        task.setStatus(Status.IN_PROGRESS);
        task.setUid(111);
        taskManager.updateTask(task);
        System.out.println("второй просмотр Task 1: " + taskManager.getTaskById(task1Id));
        System.out.println(taskManager.getHistory());
        System.out.println("---------------------");
        printAllTasks(taskManager);
        System.out.println("----------------");
        System.out.println("Просмотр task2");
        taskManager.getTaskById(task2Id);
        printAllTasks(taskManager);
        System.out.println("----------------");
        System.out.println("Просмотр эпика epic2");
        epic = taskManager.getEpicById(epic2Id);
        printAllTasks(taskManager);
        System.out.println("----------------");
        System.out.println("Просмотр субтаска subtask1Id");
        subtask = taskManager.getSubtaskById(subtask1Id);
        printAllTasks(taskManager);
        System.out.println("----------------");

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(testFile);
        System.out.println("Загруженный из файла менеджер");
        printAllTasks(newManager);

        System.out.println("----------------");
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksByEpicId(epic.getUid())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

    }

}
