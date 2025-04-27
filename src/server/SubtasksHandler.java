package server;

import com.sun.net.httpserver.HttpExchange;
import exception.ManagerSaveException;
import exception.NotFoundException;
import exception.SaveTaskException;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {

    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
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
                case GET_SUBTASKS -> {
                    List<Subtask> subtasks = taskManager.getSubtasks();
                    if (subtasks.isEmpty()) {
                        sendText(exchange, "Список подзадач пуст", OK);
                    } else {
                        sendText(exchange, createGson().toJson(subtasks), OK);
                    }
                }
                case CREATE_SUBTASK -> {
                    try {
                        Subtask subtask = createGson().fromJson(body, Subtask.class);
                        int id = taskManager.createSubtask(subtask);
                        sendText(exchange, "Создана подзадача id = " + id, CREATED);
                    } catch (SaveTaskException e) {
                        sendText(exchange, e.getMessage(), NOT_ACCEPTABLE);
                    }
                }
                case GET_SUBTASK -> {
                    try {
                        Subtask subtask = taskManager.getSubtaskById(Integer.parseInt(pathParts[2]));
                        sendText(exchange, createGson().toJson(subtask), OK);
                    } catch (NotFoundException e) {
                        sendText(exchange, e.getMessage(), NOT_FOUND);
                    }
                }
                case UPDATE_SUBTASK -> {
                    try {
                        Subtask subtask = createGson().fromJson(body, Subtask.class);
                        taskManager.updateSubtask(subtask);
                        sendCode(exchange, CREATED);
                    } catch (SaveTaskException e) {
                        sendText(exchange, e.getMessage(), NOT_ACCEPTABLE);
                    }
                }
                case DELETE_SUBTASK -> {
                    taskManager.deleteSubtaskById(Integer.parseInt(pathParts[2]));
                    sendText(exchange, "Подзадача удалена", OK);
                }
            }
        } catch (ManagerSaveException e) {
            sendText(exchange, e.getMessage(), SERVER_ERROR);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }
}
