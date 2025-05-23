package handler;

import com.sun.net.httpserver.HttpExchange;
import exception.EmptyTaskException;
import exception.NotFoundException;
import exception.SaveTaskException;
import manager.TaskManager;
import task.Epic;
import task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager) {
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
                case GET_EPICS -> {
                    List<Epic> epics = taskManager.getEpics();
                    if (epics.isEmpty()) {
                        sendText(exchange, "Список эпиков пуст", OK);
                    } else {
                        sendText(exchange, gson.toJson(epics), OK);
                    }
                }
                case CREATE_EPIC -> {
                    try {
                        Epic epic = gson.fromJson(body, Epic.class);
                        int id = taskManager.createEpic(epic);
                        sendText(exchange, "Создан эпик id = " + id, CREATED);
                    } catch (SaveTaskException e) {
                        sendText(exchange, e.getMessage(), NOT_ACCEPTABLE);
                    } catch (EmptyTaskException e) {
                        sendText(exchange, e.getMessage(), BAD_REQUEST);
                    }
                }
                case GET_EPIC -> {
                    try {
                        Epic epic = taskManager.getEpicById(Integer.parseInt(pathParts[2]));
                        sendText(exchange, gson.toJson(epic), OK);
                    } catch (NotFoundException e) {
                        sendText(exchange, e.getMessage(), NOT_FOUND);
                    }
                }
                case UPDATE_EPIC -> {
                    try {
                        Epic epic = gson.fromJson(body, Epic.class);
                        taskManager.updateEpic(epic);
                        sendCode(exchange, CREATED);
                    } catch (SaveTaskException e) {
                        sendText(exchange, e.getMessage(), NOT_ACCEPTABLE);
                    } catch (EmptyTaskException e) {
                        sendText(exchange, e.getMessage(), BAD_REQUEST);
                    }
                }
                case DELETE_EPIC -> {
                    taskManager.deleteEpicById(Integer.parseInt(pathParts[2]));
                    sendText(exchange, "Эпик удален", OK);
                }
                case GET_EPIC_SUBTASKS -> {
                    try {
                        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(Integer.parseInt(pathParts[2]));
                        sendText(exchange, gson.toJson(subtasks), OK);
                    } catch (NotFoundException e) {
                        sendText(exchange, e.getMessage(), NOT_FOUND);
                    }
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
