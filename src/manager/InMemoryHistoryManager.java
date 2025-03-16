package manager;

import task.Node;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;

    private final Map<Integer, Node> browsingHistory = new HashMap<>();

    private Node linkLast(Task task) {
        final Node oldTail = tail;
        Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
        return newNode;
    }

    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            tasks.add(node.getData());
            node = node.getNext();
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node == null) return;
        Node prevNode = node.getPrev();
        Node nextNode = node.getNext();
        if (node == tail) {
            tail = prevNode;
        }
        if (node == head) {
            head = nextNode;
        }
        if (prevNode != null) {
            prevNode.setNext(nextNode);
        }
        if (nextNode != null) {
            nextNode.setPrev(prevNode);
        }
    }

    @Override
    public void remove(int id) {
        if (browsingHistory.containsKey(id)) {
            Node node = browsingHistory.get(id);
            removeNode(node);
            browsingHistory.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(getTasks());
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        int id = task.getUid();
        remove(id);
        browsingHistory.put(id, linkLast(task));
    }
}
