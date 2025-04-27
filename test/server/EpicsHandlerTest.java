package server;

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
import task.Epic;
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
import static server.BaseHttpHandler.createGson;

public class EpicsHandlerTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(taskManager);

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
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "First epic description");
        String epic1Json = createGson().toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epic1Json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getEpics();

        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic1", tasksFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "First epic description");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic1 = taskManager.getEpicById(epic1Id);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 for Epic1", savedEpic1,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 16, 0));
        int subtask1Id = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 for Epic1", savedEpic1,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 18, 0));
        int subtask2Id = taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Epic2", "2-nd epic description");
        int epic2Id = taskManager.createEpic(epic2);
        Epic savedEpic2 = taskManager.getEpicById(epic2Id);
        Subtask subtask4 = new Subtask("Subtask4", "Subtask for Epic2", savedEpic2,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 20, 0));
        int subtask4Id = taskManager.createSubtask(subtask4);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArrayEpics = jsonElement.getAsJsonArray();

        assertEquals(2, jsonArrayEpics.size());

        List<Epic> tasksFromManager = taskManager.getEpics();
        for (int i = 0; i < jsonArrayEpics.size(); i++) {
            JsonObject jsonObjectEpic = jsonArrayEpics.get(i).getAsJsonObject();
            Epic epic = createGson().fromJson(jsonObjectEpic, Epic.class);
            Epic oldEpic = tasksFromManager.get(i);

            assertEquals(oldEpic, epic);

            JsonArray jsonArraySubtasks = jsonObjectEpic.get("subtasks").getAsJsonArray();
            for (int j = 0; j < jsonArraySubtasks.size(); j++) {
                Subtask subtask = createGson().fromJson(jsonArraySubtasks.get(j), Subtask.class);
                Subtask oldSubtask = oldEpic.getSubtasks().get(j);

                assertEquals(oldSubtask, subtask);
            }
        }
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "First epic description");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic1 = taskManager.getEpicById(epic1Id);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 for Epic1", savedEpic1,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 16, 0));
        int subtask1Id = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 for Epic1", savedEpic1,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 18, 0));
        int subtask2Id = taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Epic2", "2-nd epic description");
        int epic2Id = taskManager.createEpic(epic2);
        Epic savedEpic2 = taskManager.getEpicById(epic2Id);
        Subtask subtask4 = new Subtask("Subtask4", "Subtask for Epic2", savedEpic2,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 20, 0));
        int subtask4Id = taskManager.createSubtask(subtask4);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic2Id);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObjectEpic = jsonElement.getAsJsonObject();
        Epic epic = createGson().fromJson(jsonObjectEpic, Epic.class);

        assertEquals(epic2, epic);

        JsonArray jsonArraySubtasks = jsonObjectEpic.get("subtasks").getAsJsonArray();
        for (int j = 0; j < jsonArraySubtasks.size(); j++) {
            Subtask subtask = createGson().fromJson(jsonArraySubtasks.get(j), Subtask.class);
            Subtask oldSubtask = epic2.getSubtasks().get(j);

            assertEquals(oldSubtask, subtask);
        }

        // по несуществующему id
        url = URI.create("http://localhost:8080/epics/" + 333);
        request = HttpRequest.newBuilder().GET().uri(url)
                .header("Accept", "application/json")
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic2 = new Epic("Epic2", "2-nd epic description");
        int epic2Id = taskManager.createEpic(epic2);
        Epic savedEpic2 = taskManager.getEpicById(epic2Id);
        Subtask subtask4 = new Subtask("Subtask4", "Subtask for Epic2", savedEpic2,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 20, 0));
        int subtask4Id = taskManager.createSubtask(subtask4);

        assertEquals(savedEpic2.getName(), taskManager.getEpicById(epic2Id).getName());
        assertEquals(savedEpic2.getDescription(), taskManager.getEpicById(epic2Id).getDescription());

        savedEpic2.setName("Epic2New");
        savedEpic2.setDescription("Description New");

        assertNotEquals(savedEpic2.getName(), taskManager.getEpicById(epic2Id).getName());
        assertNotEquals(savedEpic2.getDescription(), taskManager.getEpicById(epic2Id).getDescription());


        String epicJson = createGson().toJson(savedEpic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic2Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("Epic2New", taskManager.getEpicById(epic2Id).getName());
        assertEquals("Description New", taskManager.getEpicById(epic2Id).getDescription());

    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "First epic description");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic1 = taskManager.getEpicById(epic1Id);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 for Epic1", savedEpic1,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 16, 0));
        int subtask1Id = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 for Epic1", savedEpic1,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 18, 0));
        int subtask2Id = taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Epic2", "2-nd epic description");
        int epic2Id = taskManager.createEpic(epic2);
        Epic savedEpic2 = taskManager.getEpicById(epic2Id);
        Subtask subtask4 = new Subtask("Subtask4", "Subtask for Epic2", savedEpic2,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 20, 0));
        int subtask4Id = taskManager.createSubtask(subtask4);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic1Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertThrows(NotFoundException.class, () -> taskManager.getEpicById(epic1Id));

    }

    @Test
    public void testGetSubtasksByEpicId() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic1", "First epic description");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic1 = taskManager.getEpicById(epic1Id);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1 for Epic1", savedEpic1,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 16, 0));
        int subtask1Id = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2 for Epic1", savedEpic1,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 18, 0));
        int subtask2Id = taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Epic2", "2-nd epic description");
        int epic2Id = taskManager.createEpic(epic2);
        Epic savedEpic2 = taskManager.getEpicById(epic2Id);
        Subtask subtask4 = new Subtask("Subtask4", "Subtask for Epic2", savedEpic2,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 10, 20, 0));
        int subtask4Id = taskManager.createSubtask(subtask4);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic1Id + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(url)
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArraySubtasks = jsonElement.getAsJsonArray();
        for (int j = 0; j < jsonArraySubtasks.size(); j++) {
            Subtask subtask = createGson().fromJson(jsonArraySubtasks.get(j), Subtask.class);
            Subtask oldSubtask = epic1.getSubtasks().get(j);

            assertEquals(oldSubtask, subtask);
        }

        // несуществующий эпик
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/epics/" + 404 + "/subtasks");
        request = HttpRequest.newBuilder().GET().uri(url)
                .header("Accept", "application/json")
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

    }

}
