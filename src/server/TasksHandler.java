package server;

import com.sun.net.httpserver.HttpExchange;
import exception.ManagerSaveException;
import exception.NotFoundException;
import exception.SaveTaskException;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        super();
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        Endpoint endpoint = getEndpoint(path, requestMethod);
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            switch (endpoint) {
                case GET_TASKS -> {
                    List<Task> tasks = taskManager.getTasks();
                    if (tasks.isEmpty()) {
                        sendText(exchange, "Список задач пуст", OK);
                    } else {
                        sendText(exchange, createGson().toJson(tasks), OK);
                    }
                }
                case CREATE_TASK -> {
                    try {
                        Task task = createGson().fromJson(body, Task.class);
                        int id = taskManager.createTask(task);
                        sendText(exchange, "Создана задача id = " + id, CREATED);
                    } catch (SaveTaskException e) {
                        sendText(exchange, e.getMessage(), NOT_ACCEPTABLE);
                    }
                }
                case GET_TASK -> {
                    try {
                        Task task = taskManager.getTaskById(Integer.parseInt(pathParts[2]));
                        sendText(exchange, createGson().toJson(task), OK);
                    } catch (NotFoundException e) {
                        sendText(exchange, e.getMessage(), NOT_FOUND);
                    }
                }
                case UPDATE_TASK -> {
                    try {
                        Task task = createGson().fromJson(body, Task.class);
                        taskManager.updateTask(task);
                        sendCode(exchange, CREATED);
                    } catch (SaveTaskException e) {
                        sendText(exchange, e.getMessage(), NOT_ACCEPTABLE);
                    }
                }
                case DELETE_TASK -> {
                    taskManager.deleteTaskById(Integer.parseInt(pathParts[2]));
                    sendText(exchange, "Задача удалена", OK);
                }
            }
        } catch (ManagerSaveException e) {
            sendText(exchange, e.getMessage(), SERVER_ERROR);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }
}
