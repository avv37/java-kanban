package Task;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public void setStatus(Status status) {
    }

    public void deleteSubtaskFromEpic(Subtask subtask) {
        subtasks.remove(subtask);
        changeStatusDependingOnSubtasks();
    }

    public void changeStatusDependingOnSubtasks() {
        if (subtasks.isEmpty()) {
            super.setStatus(Status.NEW);
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
            super.setStatus(Status.IN_PROGRESS);
        } else if (hasDone && !hasNew) {
            super.setStatus(Status.DONE);
        } else if (!hasDone && hasNew) {
            super.setStatus(Status.NEW);
        } else {
            super.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", uid=" + getUid() +
                ", status=" + getStatus() +
                ", subtasks=" + subtasks +
                '}';
    }
}
