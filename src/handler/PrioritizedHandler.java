package handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        Endpoint endpoint = getEndpoint(path, requestMethod);
        switch (endpoint) {
            case GET_PRIORITIZED -> {
                List<Task> taskPrioritized = taskManager.getPrioritizedTasks();
                if (taskPrioritized.isEmpty()) {
                    sendText(exchange, "Упорядоченный список пуст", OK);
                } else {
                    sendText(exchange, gson.toJson(taskPrioritized), OK);
                }
            }
            case UNKNOWN -> {
                sendText(exchange, "Такого Endpoint нет", NOT_FOUND);
            }
        }
    }
}
