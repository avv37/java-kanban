package task;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(Epic epic) {
        super(epic);
        this.getSubtasks().addAll(epic.subtasks);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public void setStatus(Status status) {
    }

    public void changeStatusDependingOnSubtasks() {
        if (subtasks.isEmpty()) {
            status = Status.NEW;
            return;
        }

        boolean hasDone = false;
        boolean hasNew = false;
        boolean hasProgress = false;
        for (Subtask subtask : getSubtasks()) {
            Status subtaskStatus = subtask.getStatus();
            if (subtaskStatus == Status.IN_PROGRESS) {
                hasProgress = true;
                break;
            }
            if (subtaskStatus == Status.DONE) {
                hasDone = true;
            } else {
                hasNew = true;
            }
        }
        if (hasProgress) {
            status = Status.IN_PROGRESS;
        } else if (hasDone && !hasNew) {
            status = Status.DONE;
        } else if (!hasDone && hasNew) {
            status = Status.NEW;
        } else {
            status = Status.IN_PROGRESS;
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", uid=" + uid +
                ", status=" + status +
                ", subtasks=" + subtasks +
                '}';
    }
}
