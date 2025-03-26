package task;

public enum Type {
    TASK,
    EPIC,
    SUBTASK;

    public static Boolean isType(String str) {
        try {
            Type value = Type.valueOf(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
