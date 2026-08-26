package nova;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {

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

    private boolean done;

    private Task(
            Type type,
            String description,
            String extraInfo,
            LocalDateTime deadline,
            boolean done) {

        this.type = type;
        this.description = description;
        this.extraInfo = extraInfo;
        this.deadline = deadline;
        this.done = done;
    }

    public static Task todo(String description) {
        return new Task(
                Type.TODO,
                description,
                "",
                null,
                false
        );
    }

    public static Task deadline(
            String description,
            LocalDateTime deadline) {

        return new Task(
                Type.DEADLINE,
                description,
                "",
                deadline,
                false
        );
    }

    public static Task event(
            String description,
            String from,
            String to) {

        return new Task(
                Type.EVENT,
                description,
                "(from: " + from + " to: " + to + ")",
                null,
                false
        );
    }

    public static Task fromStorageString(String line) {
        String[] parts = line.split("\t", -1);

        if (parts.length != 4) {
            throw new IllegalArgumentException(
                    "Invalid stored task."
            );
        }

        Type type = Type.valueOf(parts[0]);
        boolean done = parts[1].equals("1");
        String description = parts[2];

        if (type == Type.DEADLINE) {
            LocalDateTime deadline =
                    LocalDateTime.parse(parts[3]);

            return new Task(
                    type,
                    description,
                    "",
                    deadline,
                    done
            );
        }

        return new Task(
                type,
                description,
                parts[3],
                null,
                done
        );
    }

    public void markDone() {
        done = true;
    }

    public String toStorageString() {
        String storedExtraInfo;

        if (type == Type.DEADLINE) {
            storedExtraInfo = deadline.toString();
        } else {
            storedExtraInfo = extraInfo;
        }

        return type.name()
                + "\t" + (done ? "1" : "0")
                + "\t" + description
                + "\t" + storedExtraInfo;
    }

    public String toDisplayString() {
        String status = done ? "X" : " ";

        switch (type) {
            case TODO:
                return "[T][" + status + "] "
                        + description;

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