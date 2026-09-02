package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class NovaTest {

    @TempDir
    private Path tempDirectory;

    @Test
    public void getResponse_supportedCommands_returnsExpectedResponses() {
        Nova nova = new Nova(tempDirectory.resolve("nova.txt").toString());

        String addResponse = nova.getResponse("todo read book");
        String listResponse = nova.getResponse("list");
        String findResponse = nova.getResponse("find book");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertEquals("1.[T][ ] read book", listResponse);
        assertTrue(findResponse.contains("1.[T][ ] read book"));
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorMessage() {
        Nova nova = new Nova(tempDirectory.resolve("nova.txt").toString());

        assertEquals(
                "Sorry, I don't understand that command.",
                nova.getResponse("invalid")
        );
    }

    @Test
    public void getResponse_taskUpdatesAndBye_preserveExistingBehaviour() {
        Nova nova = new Nova(tempDirectory.resolve("nova.txt").toString());
        nova.getResponse("todo read book");
        nova.getResponse("event meeting /from 2pm /to 4pm");

        assertEquals(
                "Nice! I've marked this task as done.",
                nova.getResponse("mark 1")
        );
        assertTrue(nova.getResponse("list").contains("[T][X] read book"));
        assertTrue(nova.getResponse("delete 1").contains("read book"));
        assertTrue(nova.getResponse("list").contains(
                "[E][ ] meeting (from: 2pm to: 4pm)"));
        assertEquals(
                "Bye. Hope to see you again soon!",
                nova.getResponse("bye")
        );
    }

    @Test
    public void constructor_existingStorage_loadsSavedTasks() {
        Path storagePath = tempDirectory.resolve("nova.txt");
        Nova nova = new Nova(storagePath.toString());
        nova.getResponse("deadline return book /by 2026-09-10 1800");

        Nova reloadedNova = new Nova(storagePath.toString());

        assertTrue(reloadedNova.getResponse("list").contains("return book"));
    }
}
