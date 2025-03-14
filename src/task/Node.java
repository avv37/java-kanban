package task;

public class Node {

    private final Task data;
    private Node next;
    private Node prev;

    public Node(Node prev, Task data, Node next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    public Task getData() {
        return data;
    }

    public Node getNext() {
        return next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}
