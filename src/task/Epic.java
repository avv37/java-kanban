package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks = new ArrayList<>();

    private LocalDateTime endTime;


    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(Epic epic) {
        super(epic);
        this.getSubtasks().addAll(epic.subtasks);
    }

    public Epic(int uid, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(uid, name, description, status, duration, startTime);
    }

    public Epic(Epic epic, ArrayList<Subtask> subtasks) {
        super(epic);
        this.getSubtasks().addAll(subtasks);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public void setStatus(Status status) {
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }


    public void calculateStartTimeDurationEndTime() {
        if (subtasks.isEmpty()) {
            startTime = null;
            duration = Duration.ZERO;
            endTime = null;
            return;
        }
        Comparator<Subtask> comparator = (o1, o2) -> (o1.startTime.isBefore(o2.startTime) ? -1 :
                (o1.startTime.equals(o2.startTime) ? 0 : 1));
        List<Subtask> sortedSubtasks = subtasks.stream()
                .filter(subtask -> subtask.startTime != null)
                .sorted(comparator)
                .toList();
        Subtask firstSubtask = sortedSubtasks.getFirst();
        Subtask lastSubtask = sortedSubtasks.getLast();
        startTime = firstSubtask.startTime;
        endTime = lastSubtask.getEndTime();
        duration = Duration.ZERO;
        for (Subtask subtask : subtasks) {
            duration = duration.plus(subtask.getDuration());
        }
    }


    public void changeStatusDependingOnSubtasks() {
        if (subtasks.isEmpty()) {
            status = Status.NEW;
            return;
        }

        boolean hasDone = false;
        boolean hasNew = false;
        boolean hasProgress = false;
        for (Subtask subtask : subtasks) {
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
                ", duration=" + (duration == null ? 0 : duration.toMinutes()) +
                ", startTime=" + (startTime == null ? "" : startTime.format(DATE_TIME_FORMATTER)) +
                ", endTime=" + (endTime == null ? "" : endTime.format(DATE_TIME_FORMATTER)) +
                '}';
    }
}
