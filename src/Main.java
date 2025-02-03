import Task.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        System.out.println("Создание задач, эпиков и подзадач");
        Task task1 = new Task("Task 1", "First task description");
        taskManager.createTask(task1);
        Task task2 = new Task("Task 2", "Second task description");
        taskManager.createTask(task2);
        Task task3 = new Task("Task 3", "Third task description");
        taskManager.createTask(task3);

        Epic epic1 = new Epic("Epic 1", "First epic description");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask 1", "First Subtask for first Epic", epic1);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Second Subtask for first Epic", epic1);
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Epic 2", "Second epic description");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Subtask 1", "First Subtask for second Epic", epic2);
        taskManager.createSubtask(subtask3);

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
        int uid = task1.getUid();
        Task task = taskManager.getTaskById(uid);
        task.setName("Task 1.1");
        task.setDescription("First task description with change");
        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);
        System.out.println("Список задач после изменения");
        System.out.println(taskManager.getTasks());
        System.out.println("---------------------");
        System.out.println();
        System.out.println("Удаление задачи \"Task 2\"");
        uid = task2.getUid();
        task = taskManager.getTaskById(uid);
        taskManager.deleteTask(task);
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
        uid = epic2.getUid();
        Epic epic = taskManager.getEpicById(uid);
        System.out.println(epic);
        System.out.println("Изменение (обновление) эпика epic2");
        epic.setName("Эпик 2");
        epic.setDescription("Новое описание");
        epic.setStatus(Status.IN_PROGRESS);
        taskManager.updateEpic(epic);
        System.out.println("Эпик epic2 после изменения");
        System.out.println(taskManager.getEpicById(uid));
        System.out.println("Список эпиков после изменения");
        System.out.println("Статус не изменился!");
        System.out.println("----------------");

        System.out.println();
        System.out.println(taskManager.getEpics());
        System.out.println("Удаление эпика epic2");
        System.out.println("Список всех подзадач до удаления эпика");
        System.out.println(taskManager.getSubtasks());
        taskManager.deleteEpic(epic);
        System.out.println("Эпик после удаления эпика");
        System.out.println(taskManager.getEpicById(uid));
        System.out.println("Список эпиков после удаления эпика");
        System.out.println(taskManager.getEpics());
        System.out.println("Список всех подзадач после удаления эпика");
        System.out.println(taskManager.getSubtasks());
        System.out.println("----------------");

        System.out.println();
        System.out.println("Список всех подзадач");
        System.out.println(taskManager.getSubtasks());
        System.out.println("Список подзадач эпика epic1");
        System.out.println(taskManager.getSubtasksByEpicId(epic1.getUid()));
        System.out.println("Изменение (обновление) подзадачи Subtask 1");
        uid = subtask1.getUid();
        Subtask subtask = taskManager.getSubtaskById(uid);
        subtask.setName("подзадача № 1");
        subtask.setDescription("описание подзадачи № 1");
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        System.out.println("Список подзадач эпика epic1 после изменения");
        System.out.println(taskManager.getSubtasksByEpicId(epic1.getUid()));
        System.out.println("Эпик после изменения");
        System.out.println(taskManager.getEpicById(epic1.getUid()));
        System.out.println("Статус эпика изменился!");
        System.out.println();
        System.out.println("----------------");

        System.out.println("Обеим подзадачам меняю статус на DONE   taskManager.updateSubtask(subtask)");
        uid = subtask1.getUid();
        subtask = taskManager.getSubtaskById(uid);
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        uid = subtask2.getUid();
        subtask = taskManager.getSubtaskById(uid);
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        System.out.println("Эпик после изменения");
        System.out.println(taskManager.getEpicById(epic1.getUid()));
        System.out.println("Статус эпика изменился, стал DONE!");
        System.out.println("----------------");

        System.out.println();
        System.out.println("Добавляю новую подзадачу Subtask 3   taskManager.createSubtask(subtask4)");
        Subtask subtask4 = new Subtask("Subtask 3", "Third Subtask for first Epic", epic1);
        taskManager.createSubtask(subtask4);
        System.out.println("Эпик после изменения");
        System.out.println(taskManager.getEpicById(epic1.getUid()));
        System.out.println("Статус эпика изменился на IN_PROGRESS!");
        System.out.println("----------------");

        System.out.println();
        System.out.println("Удаляю подзадачу Subtask 3   taskManager.deleteSubtaskById(subtask4.getUid())");
        taskManager.deleteSubtaskById(subtask4.getUid());
        System.out.println("Эпик после изменения");
        System.out.println(taskManager.getEpicById(epic1.getUid()));
        System.out.println("Статус эпика изменился на DONE, потому что остались только такие подзадачи!");
        System.out.println("----------------");

        System.out.println("Подзадаче № 1 меняю статус на IN_PROGRESS   taskManager.updateSubtask(subtask)");
        uid = subtask1.getUid();
        subtask = taskManager.getSubtaskById(uid);
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        System.out.println("Эпик после изменения");
        System.out.println(taskManager.getEpicById(epic1.getUid()));
        System.out.println("Статус эпика изменился на IN_PROGRESS!");
        System.out.println("----------------");

        System.out.println();
        System.out.println("Удаляю все подзадачи taskManager.deleteAllSubtasks()");
        taskManager.deleteAllSubtasks();
        System.out.println("Эпик после удаления всех подзадач  taskManager.getEpicById(epic1.getUid())");
        System.out.println(taskManager.getEpicById(epic1.getUid()));
        System.out.println("Статус эпика изменился на NEW, у него нет подзадач");
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
