package server;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
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
            case GET_HISTORY -> {
                List<Task> taskHistory = taskManager.getHistory();
                if (taskHistory.isEmpty()) {
                    sendText(exchange, "История пуста", OK);
                } else {
                    sendText(exchange, createGson().toJson(taskHistory), OK);
                }
            }
        }
    }
}
