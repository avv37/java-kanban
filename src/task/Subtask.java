package task;

public class Subtask extends Task {
    private final int epicUid;

    public int getEpicUid() {
        return epicUid;
    }

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epicUid = epic.getUid();
    }

    public Subtask(Subtask subtask) {
        super(subtask);
        this.epicUid = subtask.getEpicUid();
        this.status = subtask.status;
    }

    public Subtask(int uid, String name, String description, Status status, int epicUid) {
        super(uid, name, description, status);
        this.epicUid = epicUid;
    }

    @Override
    public String toString(Type type) {
        return super.toString(type) + this.epicUid;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", uid=" + uid +
                ", status=" + status +
                ", epicUid=" + epicUid +
                '}';
    }
}
