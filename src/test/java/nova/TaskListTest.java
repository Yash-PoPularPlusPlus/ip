package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void add_validTask_increasesSize() {
        TaskList tasks = new TaskList();

        tasks.add(Task.todo("read book"));

        assertEquals(
                1,
                tasks.size()
        );
    }

    @Test
    public void delete_validTask_removesCorrectTask() {
        TaskList tasks = new TaskList();

        Task first = Task.todo("read book");
        Task second = Task.todo("write code");

        tasks.add(first);
        tasks.add(second);

        Task removed = tasks.delete(1);

        assertSame(
                first,
                removed
        );

        assertEquals(
                1,
                tasks.size()
        );

        assertSame(
                second,
                tasks.get(1)
        );
    }

    @Test
    public void delete_invalidTaskNumber_throwsException() {
        TaskList tasks = new TaskList();

        tasks.add(Task.todo("read book"));

        assertThrows(IllegalArgumentException.class, () -> tasks.delete(0));

        assertThrows(IllegalArgumentException.class, () -> tasks.delete(2));
    }

    @Test
    public void markDone_validTask_marksCorrectTask() {
        TaskList tasks = new TaskList();

        tasks.add(Task.todo("read book"));

        tasks.markDone(1);

        assertEquals(
                "[T][X] read book",
                tasks.get(1).toDisplayString()
        );
    }

    @Test
    public void markDone_invalidTaskNumber_throwsException() {
        TaskList tasks = new TaskList();

        tasks.add(Task.todo("read book"));

        assertThrows(IllegalArgumentException.class, () -> tasks.markDone(2));
    }

    @Test
    public void get_invalidTaskNumber_throwsException() {
        TaskList tasks = new TaskList();

        assertThrows(IllegalArgumentException.class, () -> tasks.get(1));
    }
}
