package server;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        super();
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        Endpoint endpoint = getEndpoint(path, requestMethod);
        switch (endpoint) {
            case GET_PRIORITIZED -> {
                List<Task> taskPrioritized = taskManager.getPrioritizedTasks();
                if (taskPrioritized.isEmpty()) {
                    sendText(exchange, "Упорядоченный список пуст", OK);
                } else {
                    sendText(exchange, createGson().toJson(taskPrioritized), OK);
                }
            }
        }
    }
}
