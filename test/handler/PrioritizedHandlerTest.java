package handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static handler.BaseHttpHandler.createGson;

public class PrioritizedHandlerTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(taskManager);
    Gson gson = createGson();

    @BeforeEach
    public void beforeEach() throws IOException {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        server.startServer();
    }

    @AfterEach
    public void afterEach() {
        server.stop();
    }

    @Test
    public void getPrioritizedTest() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "First task description", Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 12, 0));
        int task1Id = taskManager.createTask(task1);
        Task task2 = new Task("Task2", "Second task description", Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 11, 0));
        int task2Id = taskManager.createTask(task2);
        Task task3 = new Task("Task3", "Third task description", Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 10, 0));
        int task3Id = taskManager.createTask(task3);
        List<Task> taskPrioritized = taskManager.getPrioritizedTasks();


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertEquals(3, jsonArray.size());

        for (int i = 0; i < jsonArray.size(); i++) {
            Task task = gson.fromJson(jsonArray.get(i), Task.class);
            Task oldTask = taskPrioritized.get(i);
            assertEquals(oldTask, task);
        }

    }
}
