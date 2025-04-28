package handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.NotFoundException;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import task.Status;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static handler.BaseHttpHandler.createGson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TasksHandlerTest {
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
    public void testAddTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "First task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 14, 0));
        String task1Json = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task1Json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

        // добавление с пересечением
        Task task2 = new Task("Task2", "Second task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 13, 30));
        String task2Json = gson.toJson(task2);
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());

        // пустое тело запроса
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "First task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 14, 0));
        Task task2 = new Task("Task2", "Second task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 12, 0));
        Task task3 = new Task("Task3", "Third task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 10, 0));
        int task1id = taskManager.createTask(task1);
        int task2id = taskManager.createTask(task2);
        int task3id = taskManager.createTask(task3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        List<Task> tasksFromManager = taskManager.getTasks();

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
            int id = task.getUid();
            Task oldTask = tasksFromManager.get(i);
            assertEquals(oldTask, task);
        }

    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "First task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 14, 0));
        Task task2 = new Task("Task2", "Second task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 12, 0));
        Task task3 = new Task("Task3", "Third task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 10, 0));
        int task1id = taskManager.createTask(task1);
        int task2id = taskManager.createTask(task2);
        int task3id = taskManager.createTask(task3);

        // по существующему id
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task3id);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Task task = gson.fromJson(jsonObject, Task.class);
        assertEquals(task3, task);

        // по несуществующему id
        url = URI.create("http://localhost:8080/tasks/" + 8);
        request = HttpRequest.newBuilder().uri(url).GET()
                .header("Accept", "application/json")
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "First task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 14, 0));
        Task task2 = new Task("Task2", "Second task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 12, 0));
        Task task3 = new Task("Task3", "Third task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 10, 0));
        int task1id = taskManager.createTask(task1);
        int task2id = taskManager.createTask(task2);
        int task3id = taskManager.createTask(task3);

        Task task = new Task(task2id, "Task222", "Second task description222", Status.NEW, Duration.ofMinutes(10),
                LocalDateTime.of(2025, 3, 10, 12, 0));
        String taskJson = gson.toJson(task);

        assertNotEquals(taskManager.getTaskById(task2id), task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task2id);
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(taskManager.getTaskById(task2id), task);


        // с пересечением
        task = new Task(task2id, "Task222", "Second task description222", Status.NEW, Duration.ofMinutes(130),
                LocalDateTime.of(2025, 3, 10, 12, 0));
        taskJson = gson.toJson(task);

        assertNotEquals(taskManager.getTaskById(task2id), task);

        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());


        // пустое тело запроса
        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "First task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 14, 0));
        Task task2 = new Task("Task2", "Second task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 12, 0));
        Task task3 = new Task("Task3", "Third task description", Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 10, 0));
        int task1id = taskManager.createTask(task1);
        int task2id = taskManager.createTask(task2);
        int task3id = taskManager.createTask(task3);

        Task task = taskManager.getTaskById(task2id);
        assertNotNull(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task2id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertThrows(NotFoundException.class, () -> taskManager.getTaskById(task2id));

    }
}
