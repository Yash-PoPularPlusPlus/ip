package nova;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves tasks.
 */
public class Storage {

    private final Path filePath;

    /**
     * Creates storage for the specified file.
     *
     * @param filePath task storage file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from disk.
     *
     * @return loaded tasks
     * @throws IOException if the file cannot be accessed
     */
    public TaskList load() throws IOException {
        createParentDirectory();

        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
            return new TaskList();
        }

        List<Task> tasks = new ArrayList<>();
        List<String> lines = Files.readAllLines(filePath);

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }

            try {
                tasks.add(Task.fromStorageString(line));
            } catch (RuntimeException ignored) {
                // Ignore corrupted entries.
            }
        }

        return new TaskList(tasks);
    }

    /**
     * Saves tasks to disk.
     *
     * @param taskList tasks to save
     * @throws IOException if the file cannot be written
     */
    public void save(TaskList taskList) throws IOException {
        createParentDirectory();

        List<String> lines = new ArrayList<>();

        for (Task task : taskList.getAll()) {
            lines.add(task.toStorageString());
        }

        Files.write(
                filePath,
                lines,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    private void createParentDirectory()
            throws IOException {

        Path parent = filePath.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }
    }
}