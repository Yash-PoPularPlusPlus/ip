package nova;

import java.util.List;
import java.util.Scanner;

/**
 * Handles console input and formats Nova's responses.
 */
public class Ui {

    private final Scanner scanner;

    /**
     * Creates the console user interface.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Reads the next user command.
     *
     * @return User command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Formats Nova's initial greeting.
     *
     * @return Welcome message.
     */
    public static String formatWelcomeMessage() {
        return "Hello! I'm Nova." + System.lineSeparator()
                + "What can I do for you?";
    }

    /**
     * Formats all tasks with their one-based task numbers.
     *
     * @param tasks Tasks to format.
     * @return Formatted task list.
     */
    public static String formatTaskList(TaskList tasks) {
        StringBuilder response = new StringBuilder();
        for (int i = 1; i <= tasks.size(); i++) {
            if (i > 1) {
                response.append(System.lineSeparator());
            }
            response.append(i)
                    .append(".")
                    .append(tasks.get(i).toDisplayString());
        }
        return response.toString();
    }

    /**
     * Formats tasks that match a search keyword.
     *
     * @param tasks Matching tasks.
     * @return Formatted matching tasks.
     */
    public static String formatMatchingTasks(List<Task> tasks) {
        StringBuilder response = new StringBuilder(
                "Here are the matching tasks in your list:");

        for (int i = 0; i < tasks.size(); i++) {
            response.append(System.lineSeparator())
                    .append(i + 1)
                    .append(".")
                    .append(tasks.get(i).toDisplayString());
        }
        return response.toString();
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Total number of tasks.
     * @return Task-added confirmation.
     */
    public static String formatAddedTask(Task task, int taskCount) {
        return "Got it. I've added this task:"
                + System.lineSeparator()
                + task.toDisplayString()
                + System.lineSeparator()
                + "Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Formats confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param taskCount Number of remaining tasks.
     * @return Task-deleted confirmation.
     */
    public static String formatDeletedTask(Task task, int taskCount) {
        return "Noted. I've removed this task:"
                + System.lineSeparator()
                + task.toDisplayString()
                + System.lineSeparator()
                + "Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Formats confirmation that a task was marked as done.
     *
     * @return Task-marked confirmation.
     */
    public static String formatMarkedMessage() {
        return "Nice! I've marked this task as done.";
    }

    /**
     * Displays a response in the console.
     *
     * @param response Response to display.
     */
    public void showResponse(String response) {
        if (!response.isEmpty()) {
            System.out.println(response);
        }
    }

    /**
     * Formats Nova's farewell message.
     *
     * @return Farewell message.
     */
    public static String formatByeMessage() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Closes the user interface.
     */
    public void close() {
        scanner.close();
    }
}
