package handler;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {

    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
    public static final int NOT_ACCEPTABLE = 406;
    public static final int SERVER_ERROR = 500;

    protected final TaskManager taskManager;

    protected final Gson gson = createGson();

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void sendText(HttpExchange exchange, String text, int code) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(code, response.length);
            os.write(response);
        }
        exchange.close();
    }

    protected void sendCode(HttpExchange exchange, int code) throws IOException {
        exchange.sendResponseHeaders(code, 0);
        exchange.close();
    }

    public static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts[1].equals("tasks")) {
            if (pathParts.length == 2) {
                if (requestMethod.equals("GET")) {
                    return Endpoint.GET_TASKS;
                } else if (requestMethod.equals("POST")) {
                    return Endpoint.CREATE_TASK;
                }
            } else if ((pathParts.length == 3)) {
                if (requestMethod.equals("GET")) {
                    return Endpoint.GET_TASK;
                } else if (requestMethod.equals("POST")) {
                    return Endpoint.UPDATE_TASK;
                } else if (requestMethod.equals("DELETE")) {
                    return Endpoint.DELETE_TASK;
                }
            }
        } else if (pathParts[1].equals("subtasks")) {
            if (pathParts.length == 2) {
                if (requestMethod.equals("GET")) {
                    return Endpoint.GET_SUBTASKS;
                } else if (requestMethod.equals("POST")) {
                    return Endpoint.CREATE_SUBTASK;
                }
            } else if ((pathParts.length == 3)) {
                if (requestMethod.equals("GET")) {
                    return Endpoint.GET_SUBTASK;
                } else if (requestMethod.equals("POST")) {
                    return Endpoint.UPDATE_SUBTASK;
                } else if (requestMethod.equals("DELETE")) {
                    return Endpoint.DELETE_SUBTASK;
                }
            }
        } else if (pathParts[1].equals("epics")) {
            if (pathParts.length == 2) {
                if (requestMethod.equals("GET")) {
                    return Endpoint.GET_EPICS;
                } else if (requestMethod.equals("POST")) {
                    return Endpoint.CREATE_EPIC;
                }
            } else if ((pathParts.length == 3)) {
                if (requestMethod.equals("GET")) {
                    return Endpoint.GET_EPIC;
                } else if (requestMethod.equals("POST")) {
                    return Endpoint.UPDATE_EPIC;
                } else if (requestMethod.equals("DELETE")) {
                    return Endpoint.DELETE_EPIC;
                }
            } else if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                return Endpoint.GET_EPIC_SUBTASKS;
            }
        } else if (pathParts[1].equals("history")) {
            return Endpoint.GET_HISTORY;
        } else if (pathParts[1].equals("prioritized")) {
            return Endpoint.GET_PRIORITIZED;
        }

        return Endpoint.UNKNOWN;
    }

    enum Endpoint {
        GET_TASKS, CREATE_TASK, GET_TASK, DELETE_TASK, UPDATE_TASK,
        GET_SUBTASKS, CREATE_SUBTASK, GET_SUBTASK, DELETE_SUBTASK, UPDATE_SUBTASK,
        GET_EPICS, CREATE_EPIC, GET_EPIC, DELETE_EPIC, UPDATE_EPIC, GET_EPIC_SUBTASKS,
        GET_HISTORY, GET_PRIORITIZED, UNKNOWN
    }
}
