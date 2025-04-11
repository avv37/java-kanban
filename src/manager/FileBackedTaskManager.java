package manager;

import exception.ManagerReadFileException;
import exception.ManagerSaveException;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;
import task.Type;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File tasksFile;

    private static final String HEADER = "id,type,name,status,description,epic,duration,startTime\n";

    public FileBackedTaskManager(File tasksFile) {
        this.tasksFile = tasksFile;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        if (file == null) {
            throw new RuntimeException("Не передан файл.");
        } else if (!file.exists()) {
            throw new RuntimeException("Не найден файл " + file.getPath());
        }

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            List<String> lines = readFile(file);
            lines.removeFirst();

            int maxId = 0;
            for (String line : lines) {
                String[] fields = line.split(",", -1);
                // Если есть такой тип задачи
                if (Type.isType(fields[1])) {
                    int uid = Integer.parseInt(fields[0]);
                    if (uid > maxId) {
                        maxId = uid;
                    }
                    Task anyTask = fromArray(fields);
                    if (anyTask == null) continue;
                    if (anyTask instanceof Epic epic) {
                        fileBackedTaskManager.epics.put(uid, epic);
                    } else if (anyTask instanceof Subtask subtask) {
                        fileBackedTaskManager.subtasks.put(uid, subtask);
                        int epicId = Integer.parseInt(fields[5]);
                        fileBackedTaskManager.epics.get(epicId).getSubtasks().add(subtask);
                        fileBackedTaskManager.epics.get(epicId).calculateStartTimeDurationEndTime();
                    } else if (anyTask instanceof Task task) {
                        fileBackedTaskManager.tasks.put(uid, task);
                    }
                }
            }
            // устанавливаем счетчик задач в менеджере
            fileBackedTaskManager.uidCounter = maxId + 1;
        } catch (RuntimeException e) {
            throw new ManagerReadFileException("Ошибка при чтении файла " + file.getPath() + "; " + e.getMessage(), e.getCause());
        }
        return fileBackedTaskManager;
    }

    public static List<String> readFile(File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file.toString(), StandardCharsets.UTF_8))) {
            while (br.ready()) {
                lines.add(br.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    public static Task fromArray(String[] fields) {
        int uid = Integer.parseInt(fields[0]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        Duration duration = fields[6].isEmpty() || Integer.parseInt(fields[6]) == 0 ? Duration.ZERO : Duration.ofMinutes(Long.parseLong(fields[6]));
        LocalDateTime startTime = fields[7].isEmpty() ? null : LocalDateTime.parse(fields[7], Task.DATE_TIME_FORMATTER);
        if (fields[1].equalsIgnoreCase("TASK")) {
            return new Task(uid, name, description, status, duration, startTime);
        } else if (fields[1].equalsIgnoreCase("SUBTASK")) {
            int epicId = Integer.parseInt(fields[5]);
            return new Subtask(uid, name, description, status, epicId, duration, startTime);
        } else if (fields[1].equalsIgnoreCase("EPIC")) {
            return new Epic(uid, name, description, status, duration, startTime);
        } else {
            return null;
        }
    }

    protected void save() {
        try (Writer fileWriter = new FileWriter(tasksFile.toString(), StandardCharsets.UTF_8)) {
            fileWriter.write(HEADER);
            for (Task task : getTasks()) {
                fileWriter.write(task.toString(Type.TASK) + "\n");
            }
            for (Epic epic : getEpics()) {
                fileWriter.write(epic.toString(Type.EPIC) + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                fileWriter.write(subtask.toString(Type.SUBTASK) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл " + tasksFile.getAbsoluteFile(), e.getCause());
        }
    }

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int uid) {
        super.deleteTaskById(uid);
        save();
    }

    @Override
    public void deleteEpicById(int uid) {
        super.deleteEpicById(uid);
        save();
    }

    @Override
    public void deleteSubtaskById(int uid) {
        super.deleteSubtaskById(uid);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }
}
