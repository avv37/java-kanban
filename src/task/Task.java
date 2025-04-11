package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int uid = 0;
    protected Status status = Status.NEW;

    protected Duration duration = Duration.ZERO;

    protected LocalDateTime startTime;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(Task task) {
        this.name = task.name;
        this.description = task.description;
        this.uid = task.uid;
        this.status = task.status;
        this.duration = task.duration;
        this.startTime = task.startTime;
    }

    public Task(int uid, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.uid = uid;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        if (this.uid == 0) {
            this.uid = uid;
        }
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        return (startTime == null ? null : startTime.plus(duration));
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public Task fromString(String value) {
        String[] fields = value.split(",");
        int uid = Integer.parseInt(fields[0]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        Duration duration = Duration.ofMinutes(Long.parseLong(fields[6]));
        LocalDateTime startTime = fields[7].isEmpty() ? null : LocalDateTime.parse(fields[7], DATE_TIME_FORMATTER);
        return new Task(uid, name, description, status, duration, startTime);
    }

    public String toString(Type type) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s", this.uid, type.name(), this.name, this.status, this.description,
                "", this.duration == null ? 0 : this.duration.toMinutes(), this.startTime == null ? "" : this.startTime.format(DATE_TIME_FORMATTER));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        Task task = (Task) o;
        if (uid != task.uid) return false;
        if (!Objects.equals(name, task.name)) return false;
        if (!Objects.equals(description, task.description)) return false;
        if (!Objects.equals(duration, task.duration)) return false;
        if (!Objects.equals(startTime, task.startTime)) return false;
        return status == task.status;
    }

    @Override
    public int hashCode() {
        return uid;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", uid=" + uid +
                ", status=" + status +
                ", duration=" + (duration == null ? 0 : duration.toMinutes()) +
                ", startTime=" + (startTime == null ? "" : startTime.format(DATE_TIME_FORMATTER)) +
                '}';
    }
}
