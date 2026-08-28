package nova;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages Nova's collection of tasks.
 */
public class TaskList {

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the specified tasks.
     *
     * @param tasks Initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Adds a task.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes the specified task.
     *
     * @param taskNumber One-based task number.
     * @return Deleted task.
     */
    public Task delete(int taskNumber) {
        int index = getIndex(taskNumber);
        return tasks.remove(index);
    }

    /**
     * Marks the specified task as done.
     *
     * @param taskNumber One-based task number.
     */
    public void markDone(int taskNumber) {
        int index = getIndex(taskNumber);
        tasks.get(index).markDone();
    }

    /**
     * Returns the specified task.
     *
     * @param taskNumber One-based task number.
     * @return Requested task.
     */
    public Task get(int taskNumber) {
        int index = getIndex(taskNumber);
        return tasks.get(index);
    }

    /**
     * Returns all tasks as an unmodifiable list.
     *
     * @return Unmodifiable task list.
     */
    public List<Task> getAll() {
        return Collections.unmodifiableList(tasks);
    }

    private int getIndex(int taskNumber) {
        int index = taskNumber - 1;

        if (index < 0 || index >= tasks.size()) {
            throw new IllegalArgumentException(
                    "That task number does not exist.");
        }

        return index;
    }
}