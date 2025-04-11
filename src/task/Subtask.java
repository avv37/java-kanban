package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicUid;

    public int getEpicUid() {
        return epicUid;
    }

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epicUid = epic.getUid();
    }

    public Subtask(String name, String description, Epic epic, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.epicUid = epic.getUid();
    }

    public Subtask(Subtask subtask) {
        super(subtask);
        this.epicUid = subtask.getEpicUid();
        this.status = subtask.status;
        this.duration = subtask.duration;
        this.startTime = subtask.startTime;
    }

    public Subtask(int uid, String name, String description, Status status, int epicUid, Duration duration,
                   LocalDateTime startTime) {
        super(uid, name, description, status, duration, startTime);
        this.epicUid = epicUid;
    }

    @Override
    public String toString(Type type) {
        String[] parts = super.toString(type).split(",", -1);
        parts[5] = Integer.toString(this.epicUid);
        return String.join(",", parts);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", uid=" + uid +
                ", status=" + status +
                ", epicUid=" + epicUid +
                ", duration=" + (duration == null ? 0 : duration.toMinutes()) +
                ", startTime=" + (startTime == null ? "" : startTime.format(DATE_TIME_FORMATTER)) +
                '}';
    }
}
