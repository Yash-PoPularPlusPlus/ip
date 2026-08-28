package nova;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task managed by Nova.
 */
public class Task {

    /**
     * Represents the supported task types.
     */
    public enum Type {
        TODO,
        DEADLINE,
        EVENT
    }

    private static final DateTimeFormatter OUTPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy h:mma");

    private final Type type;
    private final String description;
    private final String extraInfo;
    private final LocalDateTime deadline;

    private boolean isDone;

    private Task(
            Type type,
            String description,
            String extraInfo,
            LocalDateTime deadline,
            boolean isDone) {
        this.type = type;
        this.description = description;
        this.extraInfo = extraInfo;
        this.deadline = deadline;
        this.isDone = isDone;
    }

    /**
     * Creates a todo task.
     *
     * @param description Task description.
     * @return New todo task.
     */
    public static Task todo(String description) {
        return new Task(
                Type.TODO,
                description,
                "",
                null,
                false);
    }

    /**
     * Creates a deadline task.
     *
     * @param description Task description.
     * @param deadline Deadline date and time.
     * @return New deadline task.
     */
    public static Task deadline(
            String description, LocalDateTime deadline) {
        return new Task(
                Type.DEADLINE,
                description,
                "",
                deadline,
                false);
    }

    /**
     * Creates an event task.
     *
     * @param description Event description.
     * @param from Event start.
     * @param to Event end.
     * @return New event task.
     */
    public static Task event(
            String description, String from, String to) {
        return new Task(
                Type.EVENT,
                description,
                "(from: " + from + " to: " + to + ")",
                null,
                false);
    }

    /**
     * Reconstructs a task from its stored representation.
     *
     * @param line Stored task.
     * @return Reconstructed task.
     */
    public static Task fromStorageString(String line) {
        String[] parts = line.split("\t", -1);

        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid stored task.");
        }

        Type type = Type.valueOf(parts[0]);
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        if (type == Type.DEADLINE) {
            LocalDateTime deadline = LocalDateTime.parse(parts[3]);

            return new Task(
                    type,
                    description,
                    "",
                    deadline,
                    isDone);
        }

        return new Task(
                type,
                description,
                parts[3],
                null,
                isDone);
    }

    /**
     * Marks this task as done.
     */
    public void markDone() {
        isDone = true;
    }

    /**
     * Returns the representation used to store this task.
     *
     * @return Stored representation.
     */
    public String toStorageString() {
        String storedExtraInfo;

        if (type == Type.DEADLINE) {
            storedExtraInfo = deadline.toString();
        } else {
            storedExtraInfo = extraInfo;
        }

        return type.name()
                + "\t" + (isDone ? "1" : "0")
                + "\t" + description
                + "\t" + storedExtraInfo;
    }

    /**
     * Returns the representation shown to the user.
     *
     * @return Display representation.
     */
    public String toDisplayString() {
        String status = isDone ? "X" : " ";

        switch (type) {
            case TODO:
                return "[T][" + status + "] " + description;
            case DEADLINE:
                return "[D][" + status + "] "
                        + description
                        + " (by: "
                        + deadline.format(OUTPUT_DATE_FORMAT)
                        + ")";
            case EVENT:
                return "[E][" + status + "] "
                        + description
                        + " "
                        + extraInfo;
            default:
                return description;
        }
    }
}