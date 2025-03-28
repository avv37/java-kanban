package task;

import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int uid = 0;
    protected Status status = Status.NEW;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(Task task) {
        this.name = task.name;
        this.description = task.description;
        this.uid = task.uid;
        this.status = task.status;
    }

    public Task(int uid, String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.uid = uid;
        this.status = status;
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

    public Task fromString(String value) {
        String[] fields = value.split(",");
        int uid = Integer.parseInt(fields[0]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        return new Task(uid, name, description, status);
    }

    public String toString(Type type) {
        return String.format("%d,%s,%s,%s,%s,%s", this.uid, type.name(), this.name, this.status, this.description, "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        Task task = (Task) o;
        if (uid != task.uid) return false;
        if (!Objects.equals(name, task.name)) return false;
        if (!Objects.equals(description, task.description)) return false;
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
                '}';
    }
}
