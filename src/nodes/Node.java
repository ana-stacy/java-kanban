package nodes;

public class Node<T> {

    private T data;
    private Node<T> next;
    private Node<T> prev;

    public Node(Node<T> prev, T task, Node<T> next) {
        this.prev = prev;
        this.data = task;
        this.next = next;
    }

    public T getData() {
        return data;
    }

    public Node<T> getNext() {
        return next;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setTask(T task) {
        this.data = task;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }
}
