import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {

    public static TaskManager taskManager = Managers.getDefault();

    public static void main(String[] args) {
        System.out.println("Поехали!");

        System.out.println("Создание задач, эпиков и подзадач");
        Task task = new Task("Task 1", "First task description");
        int task1Id = taskManager.createTask(task);
        task = new Task("Task 2", "Second task description");
        int task2Id = taskManager.createTask(task);
        task = new Task("Task 3", "Third task description");
        int task3Id = taskManager.createTask(task);

        Epic epic = new Epic("Epic 1", "First epic description");
        int epic1Id = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "First Subtask for first Epic", epic);
        int subtask1Id = taskManager.createSubtask(subtask);
        subtask = new Subtask("Subtask 2", "Second Subtask for first Epic", epic);
        int subtask2Id = taskManager.createSubtask(subtask);

        epic = new Epic("Epic 2", "Second epic description");
        int epic2Id = taskManager.createEpic(epic);
        subtask = new Subtask("Subtask 1 (2)", "First Subtask for second Epic", epic);
        int subtask3id = taskManager.createSubtask(subtask);

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
        System.out.println("---------------------");
        task.setName("Task 1.1");
        task.setDescription("First task description with change");
        task.setStatus(Status.IN_PROGRESS);
        task.setUid(111);
        taskManager.updateTask(task);
        System.out.println("второй просмотр Task 1: " + taskManager.getTaskById(task1Id));
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
        taskManager.deleteSubtaskById(subtask1Id);
        System.out.println("----------------");
        System.out.println("Просмотр субтаска subtask2Id");
        subtask = taskManager.getSubtaskById(subtask2Id);
        printAllTasks(taskManager);
        System.out.println("----------------");
        System.out.println("Просмотр эпика epic1");
        epic = taskManager.getEpicById(epic1Id);
        printAllTasks(taskManager);
        System.out.println("----------------");
        System.out.println("просмотр task2");
        task = taskManager.getTaskById(task2Id);
        printAllTasks(taskManager);
        System.out.println("---------------------");
        System.out.println("просмотр task1");
        task = taskManager.getTaskById(task1Id);
        printAllTasks(taskManager);
        System.out.println("---------------------");
        System.out.println("Просмотр эпика epic1");
        epic = taskManager.getEpicById(epic1Id);
        printAllTasks(taskManager);
        System.out.println("----------------");
        System.out.println("Просмотр субтаска subtask3id");
        subtask = taskManager.getSubtaskById(subtask3id);
        printAllTasks(taskManager);
        System.out.println("----------------");
        System.out.println("просмотр task3");
        task = taskManager.getTaskById(task3Id);
        printAllTasks(taskManager);
        System.out.println("----------------");
        System.out.println("Просмотр эпика epic2");
        epic = taskManager.getEpicById(epic2Id);
        printAllTasks(taskManager);
        System.out.println("----------------");
        taskManager.deleteEpicById(epic2Id);
        System.out.println("---------------------");
        System.out.println("просмотр task1");
        task = taskManager.getTaskById(task1Id);
        printAllTasks(taskManager);
        System.out.println("---------------------");
        System.out.println("Просмотр эпика epic2");
        epic = taskManager.getEpicById(epic2Id);
        printAllTasks(taskManager);
        System.out.println("----------------");
        System.out.println("Просмотр субтаска subtask3id");
        subtask = taskManager.getSubtaskById(subtask3id);
        printAllTasks(taskManager);
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

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

}
