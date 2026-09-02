package nova;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Runs the Nova chatbot.
 */
public class Nova {

    private final Storage storage;
    private final TaskList tasks;
    private final String loadingError;

    /**
     * Creates a Nova instance that stores tasks at the specified file path.
     *
     * @param filePath Path to the storage file.
     */
    public Nova(String filePath) {
        storage = new Storage(Path.of(filePath));

        TaskList loadedTasks;
        String errorMessage = "";
        try {
            loadedTasks = storage.load();
        } catch (IOException e) {
            errorMessage = "Unable to load saved tasks.";
            loadedTasks = new TaskList();
        }

        tasks = loadedTasks;
        loadingError = errorMessage;
    }

    /**
     * Runs Nova until the user exits.
     */
    public void run() {
        Ui ui = new Ui();
        ui.showResponse(getWelcomeMessage());

        boolean isRunning = true;
        while (isRunning) {
            String input = ui.readCommand();
            ui.showResponse(getResponse(input));
            isRunning = !input.equals("bye");
        }

        ui.close();
    }

    /**
     * Returns Nova's greeting and any error encountered while loading tasks.
     *
     * @return Initial message for the user.
     */
    public String getWelcomeMessage() {
        String welcomeMessage = Ui.formatWelcomeMessage();
        if (loadingError.isEmpty()) {
            return welcomeMessage;
        }
        return loadingError + System.lineSeparator() + welcomeMessage;
    }

    /**
     * Processes one user command and returns Nova's response.
     *
     * @param input User command.
     * @return Nova's response.
     */
    public String getResponse(String input) {
        try {
            Parser.ParsedCommand command = Parser.parse(input);
            return executeCommand(command);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (IOException e) {
            return "Unable to save tasks.";
        }
    }

    /**
     * Executes a parsed command using the current task list and storage.
     *
     * @param command Parsed user command.
     * @return Nova's response to the command.
     * @throws IOException If the updated task list cannot be saved.
     */
    private String executeCommand(Parser.ParsedCommand command)
            throws IOException {
        switch (command.getType()) {
            case BYE:
                return Ui.formatByeMessage();
            case LIST:
                return Ui.formatTaskList(tasks);
            case MARK:
                tasks.markDone(command.getTaskNumber());
                storage.save(tasks);
                return Ui.formatMarkedMessage();
            case DELETE:
                Task removedTask = tasks.delete(command.getTaskNumber());
                storage.save(tasks);
                return Ui.formatDeletedTask(removedTask, tasks.size());
            case TODO:
                Task todo = Task.todo(command.getDescription());
                tasks.add(todo);
                storage.save(tasks);
                return Ui.formatAddedTask(todo, tasks.size());
            case DEADLINE:
                Task deadline = Task.deadline(
                        command.getDescription(),
                        command.getDeadline());
                tasks.add(deadline);
                storage.save(tasks);
                return Ui.formatAddedTask(deadline, tasks.size());
            case EVENT:
                Task event = Task.event(
                        command.getDescription(),
                        command.getFrom(),
                        command.getTo());
                tasks.add(event);
                storage.save(tasks);
                return Ui.formatAddedTask(event, tasks.size());
            case FIND:
                List<Task> matches = tasks.find(command.getDescription());
                return Ui.formatMatchingTasks(matches);
            case UNKNOWN:
                return "Sorry, I don't understand that command.";
            default:
                return "";
        }
    }

    /**
     * Starts Nova using the default storage file.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        new Nova("data/nova.txt").run();
    }
}
