package Task;

public class Subtask extends Task {
    private final int epicUid;

    public int getEpicUid() {
        return epicUid;
    }

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epicUid = epic.getUid();
        epic.getSubtasks().add(this);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", uid=" + getUid() +
                ", status=" + getStatus() +
                ", epicUid=" + epicUid +
                '}';
    }
}
