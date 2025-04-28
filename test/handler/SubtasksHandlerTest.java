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
import task.Epic;
import task.Status;
import task.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static handler.BaseHttpHandler.createGson;

public class SubtasksHandlerTest {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "First epic description");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic1 = taskManager.getEpicById(epic1Id);

        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 16, 0));
        String subtask1Json = gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtask1Json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Subtask1", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");

        // добавление с пересечением
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 for Epic1", savedEpic1, Duration.ofMinutes(160),
                LocalDateTime.of(2025, 3, 10, 15, 0));
        String subtask2Json = gson.toJson(subtask2);

        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtask2Json))
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
    public void testGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "First epic description");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic1 = taskManager.getEpicById(epic1Id);

        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 16, 0));

        int subtask1Id = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 18, 0));
        int subtask2Id = taskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask3", "Subtask3 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 17, 0));
        int subtask3Id = taskManager.createSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        List<Subtask> subtasksFromManager = taskManager.getSubtasks();

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertEquals(3, jsonArray.size());

        for (int i = 0; i < jsonArray.size(); i++) {
            Subtask subtask = gson.fromJson(jsonArray.get(i), Subtask.class);
            int id = subtask.getUid();
            Subtask oldSubtask = subtasksFromManager.get(i);
            assertEquals(oldSubtask, subtask);
        }
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "First epic description");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic1 = taskManager.getEpicById(epic1Id);

        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 16, 0));

        int subtask1Id = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 18, 0));
        int subtask2Id = taskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask3", "Subtask3 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 17, 0));
        int subtask3Id = taskManager.createSubtask(subtask3);


        Subtask subtask = new Subtask(subtask2Id, "Subtask2222", "Subtask222 for Epic1", Status.NEW,
                epic1Id, Duration.ofMinutes(10), LocalDateTime.of(2025, 3, 10, 18, 0));
        String subtaskJson = gson.toJson(subtask);

        assertNotEquals(taskManager.getSubtaskById(subtask2Id), subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask2Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(taskManager.getSubtaskById(subtask2Id), subtask);

        // добавление с пересечением
        subtask = new Subtask(subtask2Id, "Subtask2222", "Subtask222 for Epic1", Status.NEW,
                epic1Id, Duration.ofMinutes(100), LocalDateTime.of(2025, 3, 10, 16, 0));
        subtaskJson = gson.toJson(subtask);

        assertNotEquals(taskManager.getSubtaskById(subtask2Id), subtask);

        request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
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
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "First epic description");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic1 = taskManager.getEpicById(epic1Id);

        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 16, 0));

        int subtask1Id = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 18, 0));
        int subtask2Id = taskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask3", "Subtask3 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 17, 0));
        int subtask3Id = taskManager.createSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask2Id);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Subtask subtask = gson.fromJson(jsonObject, Subtask.class);
        assertEquals(subtask2, subtask);

        // по несуществующему id
        url = URI.create("http://localhost:8080/subtasks/" + 222);
        request = HttpRequest.newBuilder().GET().uri(url)
                .header("Accept", "application/json")
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "First epic description");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic1 = taskManager.getEpicById(epic1Id);

        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 16, 0));

        int subtask1Id = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 18, 0));
        int subtask2Id = taskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask3", "Subtask3 for Epic1", savedEpic1, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 3, 10, 17, 0));
        int subtask3Id = taskManager.createSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask2Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertThrows(NotFoundException.class, () -> taskManager.getSubtaskById(subtask2Id));
    }
}
