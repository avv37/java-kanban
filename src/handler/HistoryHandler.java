package handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        Endpoint endpoint = getEndpoint(path, requestMethod);
        switch (endpoint) {
            case GET_HISTORY -> {
                List<Task> taskHistory = taskManager.getHistory();
                if (taskHistory.isEmpty()) {
                    sendText(exchange, "История пуста", OK);
                } else {
                    sendText(exchange, gson.toJson(taskHistory), OK);
                }
            }
            case UNKNOWN -> {
                sendText(exchange, "Такого Endpoint нет", NOT_FOUND);
            }
        }
    }
}
