package handler;

import com.sun.net.httpserver.HttpExchange;
import exception.EmptyTaskException;
import exception.NotFoundException;
import exception.SaveTaskException;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
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
                        sendText(exchange, gson.toJson(subtasks), OK);
                    }
                }
                case CREATE_SUBTASK -> {
                    try {
                        Subtask subtask = gson.fromJson(body, Subtask.class);
                        int id = taskManager.createSubtask(subtask);
                        sendText(exchange, "Создана подзадача id = " + id, CREATED);
                    } catch (SaveTaskException e) {
                        sendText(exchange, e.getMessage(), NOT_ACCEPTABLE);
                    } catch (EmptyTaskException e) {
                        sendText(exchange, e.getMessage(), BAD_REQUEST);
                    }
                }
                case GET_SUBTASK -> {
                    try {
                        Subtask subtask = taskManager.getSubtaskById(Integer.parseInt(pathParts[2]));
                        sendText(exchange, gson.toJson(subtask), OK);
                    } catch (NotFoundException e) {
                        sendText(exchange, e.getMessage(), NOT_FOUND);
                    }
                }
                case UPDATE_SUBTASK -> {
                    try {
                        Subtask subtask = gson.fromJson(body, Subtask.class);
                        taskManager.updateSubtask(subtask);
                        sendCode(exchange, CREATED);
                    } catch (SaveTaskException e) {
                        sendText(exchange, e.getMessage(), NOT_ACCEPTABLE);
                    } catch (EmptyTaskException e) {
                        sendText(exchange, e.getMessage(), BAD_REQUEST);
                    }
                }
                case DELETE_SUBTASK -> {
                    taskManager.deleteSubtaskById(Integer.parseInt(pathParts[2]));
                    sendText(exchange, "Подзадача удалена", OK);
                }
                case UNKNOWN -> {
                    sendText(exchange, "Такого Endpoint нет", NOT_FOUND);
                }
            }
        } catch (Exception e) {
            sendText(exchange, e.getMessage(), SERVER_ERROR);
        }
    }
}
