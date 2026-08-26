import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Parser {

    private static final DateTimeFormatter INPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    public enum CommandType {
        BYE,
        LIST,
        MARK,
        DELETE,
        TODO,
        DEADLINE,
        EVENT,
        UNKNOWN
    }

    public static ParsedCommand parse(String input) {

        if (input.equals("bye")) {
            return new ParsedCommand(CommandType.BYE);

        } else if (input.equals("list")) {
            return new ParsedCommand(CommandType.LIST);

        } else if (input.startsWith("mark ")) {
            return ParsedCommand.withTaskNumber(
                    CommandType.MARK,
                    parseTaskNumber(input.substring(5))
            );

        } else if (input.startsWith("delete ")) {
            return ParsedCommand.withTaskNumber(
                    CommandType.DELETE,
                    parseTaskNumber(input.substring(7))
            );

        } else if (input.trim().equals("todo")) {
            throw new IllegalArgumentException(
                    "Please provide a description for the todo."
            );

        } else if (input.startsWith("todo ")) {
            String description = input.substring(5);

            return ParsedCommand.withDescription(
                    CommandType.TODO,
                    description
            );

        } else if (input.startsWith("deadline ")) {
            return parseDeadline(input.substring(9));

        } else if (input.startsWith("event ")) {
            return parseEvent(input.substring(6));
        }

        return new ParsedCommand(CommandType.UNKNOWN);
    }

    private static ParsedCommand parseDeadline(
            String content) {

        String[] parts =
                content.split(" /by ", 2);

        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "Please use: deadline DESCRIPTION "
                            + "/by yyyy-MM-dd HHmm"
            );
        }

        try {
            LocalDateTime deadline =
                    LocalDateTime.parse(
                            parts[1],
                            INPUT_DATE_FORMAT
                    );

            return ParsedCommand.withDeadline(
                    parts[0],
                    deadline
            );

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Please enter the deadline "
                            + "as yyyy-MM-dd HHmm."
            );
        }
    }

    private static ParsedCommand parseEvent(
            String content) {

        String[] fromParts =
                content.split(" /from ", 2);

        if (fromParts.length != 2) {
            throw new IllegalArgumentException(
                    "Please provide both /from and /to."
            );
        }

        String[] toParts =
                fromParts[1].split(" /to ", 2);

        if (toParts.length != 2) {
            throw new IllegalArgumentException(
                    "Please provide both /from and /to."
            );
        }

        return ParsedCommand.withEvent(
                fromParts[0],
                toParts[0],
                toParts[1]
        );
    }

    private static int parseTaskNumber(
            String numberText) {

        try {
            return Integer.parseInt(numberText);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "That task number does not exist."
            );
        }
    }

    public static class ParsedCommand {

        private final CommandType type;
        private int taskNumber;
        private String description;
        private LocalDateTime deadline;
        private String from;
        private String to;

        public ParsedCommand(CommandType type) {
            this.type = type;
        }

        public static ParsedCommand withTaskNumber(
                CommandType type,
                int taskNumber) {

            ParsedCommand command =
                    new ParsedCommand(type);

            command.taskNumber = taskNumber;
            return command;
        }

        public static ParsedCommand withDescription(
                CommandType type,
                String description) {

            ParsedCommand command =
                    new ParsedCommand(type);

            command.description = description;
            return command;
        }

        public static ParsedCommand withDeadline(
                String description,
                LocalDateTime deadline) {

            ParsedCommand command =
                    new ParsedCommand(
                            CommandType.DEADLINE
                    );

            command.description = description;
            command.deadline = deadline;

            return command;
        }

        public static ParsedCommand withEvent(
                String description,
                String from,
                String to) {

            ParsedCommand command =
                    new ParsedCommand(
                            CommandType.EVENT
                    );

            command.description = description;
            command.from = from;
            command.to = to;

            return command;
        }

        public CommandType getType() {
            return type;
        }

        public int getTaskNumber() {
            return taskNumber;
        }

        public String getDescription() {
            return description;
        }

        public LocalDateTime getDeadline() {
            return deadline;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }
    }
}