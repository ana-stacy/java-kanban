package taskManagers;

import tasks.Task;

import java.util.*;

import nodes.Node;

public class InMemoryHistoryManager implements HistoryManager {

    HandMadeLinkedList history = new HandMadeLinkedList();


    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public void add(Task task) {
        history.linklast(task);
    }

    @Override
    public void remove(int id) {
        history.deleteTaskIfExists(id);

    }

    public static class HandMadeLinkedList {

        private final Map<Integer, Node<Task>> historyMap = new HashMap<>();

        private Node<Task> head = null;
        private Node<Task> tail = null;


        public void linklast(Task task) {
            deleteTaskIfExists(task.getId());

            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node<>(oldTail, task, null);
            tail = newNode;

            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.setNext(newNode);
            }

            historyMap.put(task.getId(), newNode);
        }

        public void deleteTaskIfExists (int id) {
            Node<Task> taskNode = historyMap.get(id);
            if (taskNode != null) {
                removeNode(taskNode);
                historyMap.remove(id);
            }
        }

        public List<Task> getTasks() {
            List<Task> historyTaskList = new ArrayList<>();
            Node<Task> currentNode = head;
            while (currentNode != null) {
                historyTaskList.add(currentNode.getData());
                currentNode = currentNode.getNext();
            }
            return historyTaskList;
        }

        public void removeNode(Node<Task> taskNode) {
            final Node<Task> prev = taskNode.getPrev();
            final Node<Task> next = taskNode.getNext();

             if (prev == null && next == null) {
                 head = null;
                 tail = null;
             } else if (next == null) {
                 tail = prev;
                 tail.setNext(null);
             } else if (prev == null) {
                 head = next;
                 head.setPrev(null);
             } else {
                 prev.setNext(next);
                 next.setPrev(prev);
             }

        }

        public Node<Task> getHead() {
            return head;
        }

        public Node<Task> getTail() {
            return tail;
        }

        public Map<Integer, Node<Task>> getHistoryMap() {
            return historyMap;
        }
    }

}
