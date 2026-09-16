package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class UiTest {

    @Test
    public void formatTaskList_emptyAndPopulatedLists_formatsOneBasedNumbers() {
        assertEquals("", Ui.formatTaskList(new TaskList()));

        TaskList tasks = new TaskList(List.of(
                Task.todo("read book"),
                Task.event("consultation", "2pm", "4pm")));
        String expected = String.join(System.lineSeparator(),
                "1.[T][ ] read book",
                "2.[E][ ] consultation (from: 2pm to: 4pm)");

        assertEquals(expected, Ui.formatTaskList(tasks));
    }

    @Test
    public void formatMatchingTasks_emptyAndPopulatedLists_formatsHeaderAndNumbers() {
        String header = "Here are the matching tasks in your list:";
        assertEquals(header, Ui.formatMatchingTasks(List.of()));

        String expected = String.join(System.lineSeparator(),
                header,
                "1.[T][ ] read book",
                "2.[T][ ] return book");
        assertEquals(expected, Ui.formatMatchingTasks(List.of(
                Task.todo("read book"), Task.todo("return book"))));
    }

    @Test
    public void formatTaskConfirmations_validTasks_includeDetailsAndCounts() {
        Task task = Task.todo("read book");

        assertEquals(String.join(System.lineSeparator(),
                "Got it. I've added this task:",
                "[T][ ] read book",
                "Now you have 2 tasks in the list."),
                Ui.formatAddedTask(task, 2));
        assertEquals(String.join(System.lineSeparator(),
                "Noted. I've removed this task:",
                "[T][ ] read book",
                "Now you have 1 tasks in the list."),
                Ui.formatDeletedTask(task, 1));
        assertEquals(String.join(System.lineSeparator(),
                "Got it. I've updated this task:",
                "[T][ ] read book"),
                Ui.formatUpdatedTask(task));
    }

    @Test
    public void formatFixedMessages_returnsExpectedText() {
        assertEquals(String.join(System.lineSeparator(),
                "Hello! I'm Nova.",
                "What can I do for you?"), Ui.formatWelcomeMessage());
        assertEquals("Nice! I've marked this task as done.",
                Ui.formatMarkedMessage());
        assertEquals("Bye. Hope to see you again soon!",
                Ui.formatByeMessage());
    }
}
