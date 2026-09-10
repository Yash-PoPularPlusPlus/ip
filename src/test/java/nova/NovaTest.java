package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class NovaTest {

    @TempDir
    private Path tempDirectory;

    @Test
    public void getResponse_update_preservesStoredDetailsAndSearch() throws IOException {
        Path storagePath = tempDirectory.resolve("nova.txt");
        String original = "DEADLINE\t1\toriginal\t2026-09-10T18:00";
        Files.writeString(storagePath, original + System.lineSeparator());
        Nova nova = new Nova(storagePath.toString());

        String response = nova.getResponse("update 1 revised homework");
        String expectedStored = original.replace("original", "revised homework");
        assertEquals(String.join(System.lineSeparator(), "Got it. I've updated this task:",
                Task.fromStorageString(expectedStored).toDisplayString()), response);
        assertEquals(List.of(expectedStored), Files.readAllLines(storagePath));
        Nova reloaded = new Nova(storagePath.toString());
        assertEquals(nova.getResponse("list"), reloaded.getResponse("list"));
        assertTrue(reloaded.getResponse("find homework").contains("revised homework"));
        assertEquals("Here are the matching tasks in your list:", reloaded.getResponse("find original"));
    }

    @Test
    public void getResponse_invalidUpdate_preservesMemoryAndStorage() throws IOException {
        Path storagePath = tempDirectory.resolve("nova.txt");
        Nova nova = new Nova(storagePath.toString());
        nova.getResponse("todo original");
        String saved = Files.readString(storagePath);

        assertEquals("Please use: update NUMBER DESCRIPTION", nova.getResponse("update 1"));
        assertEquals("That task number does not exist.", nova.getResponse("update 2 revised"));
        assertEquals("The description cannot contain tabs or line breaks.",
                nova.getResponse("update 1 bad\tdescription"));
        assertEquals("1.[T][ ] original", nova.getResponse("list"));
        assertEquals(saved, Files.readString(storagePath));
    }

    @Test
    public void getResponse_updateWhenStorageUnavailable_returnsSaveError() throws IOException {
        Path blockedParent = tempDirectory.resolve("blocked");
        Files.writeString(blockedParent, "Not a directory");
        Nova nova = new Nova(blockedParent.resolve("nova.txt").toString());
        nova.getResponse("todo original");

        assertEquals("Unable to save tasks.", nova.getResponse("update 1 revised"));
    }

    @Test
    public void getResponse_addEachTaskType_preservesConfirmationAndStorage() throws IOException {
        Path storagePath = tempDirectory.resolve("nova.txt");
        Nova nova = new Nova(storagePath.toString());
        String[] commands = {
            "todo read book",
            "deadline return book /by 2026-09-10 1800",
            "event meeting /from 2pm /to 4pm"
        };
        String[] displays = {
            "[T][ ] read book",
            Task.deadline("return book", LocalDateTime.of(2026, 9, 10, 18, 0)).toDisplayString(),
            "[E][ ] meeting (from: 2pm to: 4pm)"
        };

        for (int i = 0; i < commands.length; i++) {
            int taskCount = i + 1;
            String expected = String.join(System.lineSeparator(),
                    "Got it. I've added this task:", displays[i],
                    "Now you have " + taskCount + " tasks in the list.");
            assertEquals(expected, nova.getResponse(commands[i]));
            assertEquals(taskCount, Files.readAllLines(storagePath).size());
            Nova reloadedNova = new Nova(storagePath.toString());
            assertEquals(nova.getResponse("list"), reloadedNova.getResponse("list"));
        }
    }

    @Test
    public void getResponse_addTaskWhenStorageUnavailable_returnsSaveError() throws IOException {
        Path blockedParent = tempDirectory.resolve("blocked");
        Files.writeString(blockedParent, "This file cannot be a storage directory.");
        Nova nova = new Nova(blockedParent.resolve("nova.txt").toString());

        assertEquals("Unable to save tasks.", nova.getResponse("todo read book"));
        assertEquals("Unable to save tasks.",
                nova.getResponse("deadline return book /by 2026-09-10 1800"));
        assertEquals("Unable to save tasks.",
                nova.getResponse("event meeting /from 2pm /to 4pm"));
    }

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
