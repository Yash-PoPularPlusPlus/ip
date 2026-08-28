package nova;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Runs the Nova chatbot.
 */
public class Nova {

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates a Nova instance that stores tasks at the specified file path.
     *
     * @param filePath Path to the storage file.
     */
    public Nova(String filePath) {
        ui = new Ui();
        storage = new Storage(Path.of(filePath));

        TaskList loadedTasks;
        try {
            loadedTasks = storage.load();
        } catch (IOException e) {
            ui.showError("Unable to load saved tasks.");
            loadedTasks = new TaskList();
        }

        tasks = loadedTasks;
    }

    /**
     * Runs Nova until the user exits.
     */
    public void run() {
        ui.showWelcome();

        boolean isRunning = true;
        while (isRunning) {
            String input = ui.readCommand();

            try {
                Parser.ParsedCommand command = Parser.parse(input);

                switch (command.getType()) {
                    case BYE:
                        ui.showBye();
                        isRunning = false;
                        break;
                    case LIST:
                        ui.showTaskList(tasks);
                        break;
                    case MARK:
                        tasks.markDone(command.getTaskNumber());
                        storage.save(tasks);
                        ui.showMarked();
                        break;
                    case DELETE:
                        Task removedTask = tasks.delete(command.getTaskNumber());
                        storage.save(tasks);
                        ui.showDeletedTask(removedTask, tasks.size());
                        break;
                    case TODO:
                        Task todo = Task.todo(command.getDescription());
                        tasks.add(todo);
                        storage.save(tasks);
                        ui.showAddedTask(todo, tasks.size());
                        break;
                    case DEADLINE:
                        Task deadline = Task.deadline(
                                command.getDescription(),
                                command.getDeadline());
                        tasks.add(deadline);
                        storage.save(tasks);
                        ui.showAddedTask(deadline, tasks.size());
                        break;
                    case EVENT:
                        Task event = Task.event(
                                command.getDescription(),
                                command.getFrom(),
                                command.getTo());
                        tasks.add(event);
                        storage.save(tasks);
                        ui.showAddedTask(event, tasks.size());
                        break;
                    case UNKNOWN:
                        ui.showError("Sorry, I don't understand that command.");
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException e) {
                ui.showError(e.getMessage());
            } catch (IOException e) {
                ui.showError("Unable to save tasks.");
            }
        }

        ui.close();
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