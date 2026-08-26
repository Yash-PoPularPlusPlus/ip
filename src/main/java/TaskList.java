import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskList {

    private final List<Task> tasks;

    public TaskList() {
        tasks = new ArrayList<>();
    }

    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public int size() {
        return tasks.size();
    }

    public void add(Task task) {
        tasks.add(task);
    }

    public Task delete(int taskNumber) {
        int index = getIndex(taskNumber);
        return tasks.remove(index);
    }

    public void markDone(int taskNumber) {
        int index = getIndex(taskNumber);
        tasks.get(index).markDone();
    }

    public Task get(int taskNumber) {
        int index = getIndex(taskNumber);
        return tasks.get(index);
    }

    public List<Task> getAll() {
        return Collections.unmodifiableList(tasks);
    }

    private int getIndex(int taskNumber) {
        int index = taskNumber - 1;

        if (index < 0 || index >= tasks.size()) {
            throw new IllegalArgumentException(
                    "That task number does not exist."
            );
        }

        return index;
    }
}