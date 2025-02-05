import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

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
        System.out.println("Изменение (обновление) задачи \"Task 1\"   taskManager.updateTask(task)");
        System.out.println("Список задач до изменения");
        System.out.println(taskManager.getTasks());
        System.out.println("task1 uid = " + task1Id);
        task = taskManager.getTaskById(task1Id);
        System.out.println(task);
        task.setName("Task 1.1");
        task.setDescription("First task description with change");
        task.setStatus(Status.IN_PROGRESS);
        task.setUid(111);
        System.out.println("Список всех задач task до апдейта  taskManager.getTasks()");
        System.out.println(taskManager.getTasks());
        taskManager.updateTask(task);
        System.out.println("Список задач после изменения");
        System.out.println(taskManager.getTasks());
        System.out.println("id задачи не изменился!");
        System.out.println("---------------------");
        System.out.println();
        System.out.println("Удаление задачи \"Task 2\"");
        taskManager.deleteTaskById(task2Id);
        System.out.println("Список задач после изменения");
        System.out.println(taskManager.getTasks());
        System.out.println("---------------------");

        System.out.println();
        System.out.println("Удаление всех задач");
        taskManager.deleteAllTasks();
        System.out.println("Список задач после изменения");
        System.out.println(taskManager.getTasks());
        System.out.println("---------------------");

        System.out.println();
        System.out.println("Просмотр эпика epic2");
        epic = taskManager.getEpicById(epic2Id);
        System.out.println(epic);
        System.out.println("Изменение (обновление) эпика epic2: Эпик 2, Новое описание, IN_PROGRESS, 222");
        epic.setName("Эпик 2");
        epic.setDescription("Новое описание");
        epic.setStatus(Status.IN_PROGRESS);
        epic.setUid(222);
        System.out.println("Изменённый объект epic для taskManager.updateEpic  = " + epic);
        System.out.println("taskManager.taskManager.getEpicById(epic2Id) " + taskManager.getEpicById(epic2Id));
        System.out.println("Запуск taskManager.updateEpic(epic)");
        taskManager.updateEpic(epic);
        System.out.println("Эпик epic2 после изменения");
        System.out.println(taskManager.getEpicById(epic2Id));
        System.out.println("Список эпиков после изменения");
        System.out.println("Статус не изменился!");
        System.out.println("id не изменился!");
        System.out.println("----------------");

        System.out.println();
        System.out.println(taskManager.getEpics());
        System.out.println("Удаление эпика epic2");
        System.out.println("Список всех подзадач до удаления эпика");
        System.out.println(taskManager.getSubtasks());
        taskManager.deleteEpicById(epic2Id);
        System.out.println("Эпик после удаления эпика");
        System.out.println(taskManager.getEpicById(epic2Id));
        System.out.println("Список эпиков после удаления эпика");
        System.out.println(taskManager.getEpics());
        System.out.println("Список всех подзадач после удаления эпика");
        System.out.println(taskManager.getSubtasks());
        System.out.println("----------------");

        System.out.println();
        System.out.println("Список подзадач эпика epic1");
        System.out.println(taskManager.getSubtasksByEpicId(epic1Id));
        System.out.println("Изменение (обновление) подзадачи Subtask 1: подзадача № 1, IN_PROGRESS");
        System.out.println("subtask1Id = " + subtask1Id);
        subtask = taskManager.getSubtaskById(subtask1Id);
        System.out.println("объект subtask до изменений = " + subtask);
        subtask.setName("подзадача № 1");
        subtask.setDescription("описание подзадачи № 1");
        subtask.setStatus(Status.IN_PROGRESS);
        System.out.println("Изменённый объект subtask для taskManager.updateSubtask  = " + subtask);
        System.out.println("taskManager.getSubtasksByEpicId(epic1Id) " + taskManager.getSubtasksByEpicId(epic1Id));
        System.out.println("taskManager.getSubtaskById(subtask1Id) " + taskManager.getSubtaskById(subtask1Id));
        System.out.println("Запуск taskManager.updateSubtask(subtask)");
        taskManager.updateSubtask(subtask);

        System.out.println("Список подзадач эпика epic1 после изменения");
        System.out.println(taskManager.getSubtasksByEpicId(epic1Id));
        System.out.println("Эпик после изменения");
        System.out.println(taskManager.getEpicById(epic1Id));
        System.out.println("Статус эпика изменился!");
        System.out.println();
        System.out.println("----------------");

        System.out.println("Обеим подзадачам меняю статус на DONE   taskManager.updateSubtask(subtask)");
        subtask = taskManager.getSubtaskById(subtask1Id);
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        subtask = taskManager.getSubtaskById(subtask2Id);
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        System.out.println("Список подзадач эпика epic1 после изменения");
        System.out.println(taskManager.getSubtasksByEpicId(epic1Id));
        System.out.println("Эпик после изменения");
        System.out.println(taskManager.getEpicById(epic1Id));
        System.out.println("Статус эпика изменился, стал DONE!");
        System.out.println("----------------");

        System.out.println();
        System.out.println("Добавляю новую подзадачу Subtask 3   taskManager.createSubtask(subtask4)");
        epic = taskManager.getEpicById(epic1Id);
        subtask = new Subtask("Subtask 3", "Third Subtask for first Epic", epic);
        int subtask4Id = taskManager.createSubtask(subtask);
        System.out.println("Эпик после изменения");
        System.out.println(taskManager.getEpicById(epic1Id));
        System.out.println("Статус эпика изменился на IN_PROGRESS!");
        System.out.println("----------------");

        System.out.println();
        System.out.println("Удаляю подзадачу Subtask 3   taskManager.deleteSubtaskById(subtask4.getUid())");
        taskManager.deleteSubtaskById(subtask4Id);
        System.out.println("Эпик после изменения");
        System.out.println(taskManager.getEpicById(epic1Id));
        System.out.println("Список подзадач эпика epic1 после изменения");
        System.out.println(taskManager.getSubtasksByEpicId(epic1Id));
        System.out.println("Статус эпика изменился на DONE, потому что остались только такие подзадачи!");
        System.out.println("----------------");

        System.out.println("Все подзадачи taskManager.getSubtasks():");
        System.out.println(taskManager.getSubtasks());
        System.out.println("Подзадаче № 1 меняю статус на IN_PROGRESS   taskManager.updateSubtask(subtask)");
        subtask = taskManager.getSubtaskById(subtask4Id);
        System.out.println("subtask4Id subtask = " + subtask);
        if (subtask == null) {
            subtask = taskManager.getSubtaskById(subtask1Id);
            System.out.println("subtask1Id subtask = " + subtask);
        }
        subtask.setStatus(Status.IN_PROGRESS);
        System.out.println("Изменённый объект subtask для taskManager.updateSubtask  = " + subtask);
        System.out.println("taskManager.getSubtasksByEpicId(epic1Id) " + taskManager.getSubtasksByEpicId(epic1Id));
        System.out.println("taskManager.getSubtaskById(subtask1Id) " + taskManager.getSubtaskById(subtask1Id));
        System.out.println("Запуск taskManager.updateSubtask(subtask)");

        taskManager.updateSubtask(subtask);
        System.out.println("Эпик после изменения");
        System.out.println(taskManager.getEpicById(epic1Id));
        System.out.println("Статус эпика изменился на IN_PROGRESS!");
        System.out.println("Список подзадач эпика epic1 после изменения");
        System.out.println(taskManager.getSubtasksByEpicId(epic1Id));
        System.out.println("Все подзадачи taskManager.getSubtasks():");
        System.out.println(taskManager.getSubtasks());
        System.out.println("----------------");

        System.out.println();
        System.out.println("Удаляю все подзадачи taskManager.deleteAllSubtasks()");
        taskManager.deleteAllSubtasks();
        System.out.println("Эпик после удаления всех подзадач  taskManager.getEpicById(epic1.getUid())");
        System.out.println(taskManager.getEpicById(epic1Id));
        System.out.println("Статус эпика изменился на NEW, у него нет подзадач");
        System.out.println("Список подзадач  epic1 после изменения");
        System.out.println(taskManager.getSubtasksByEpicId(epic1Id));
        System.out.println("Все подзадачи taskManager.getSubtasks():");
        System.out.println(taskManager.getSubtasks());
        System.out.println("----------------");

        System.out.println();
        System.out.println("Удаляю все эпики taskManager.deleteAllEpics()");
        taskManager.deleteAllEpics();

        System.out.println("Все эпики taskManager.getEpics():");
        System.out.println(taskManager.getEpics());
        System.out.println("Все подзадачи taskManager.getSubtasks():");
        System.out.println(taskManager.getSubtasks());

    }

}
