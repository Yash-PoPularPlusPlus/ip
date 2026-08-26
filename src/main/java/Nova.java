import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Nova {

    private static final Path DATA_FILE = Path.of("data", "nova.txt");

    private static final DateTimeFormatter INPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private static final DateTimeFormatter OUTPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy h:mma");

    private enum TaskType {
        TODO,
        DEADLINE,
        EVENT
    }

    private static int loadTasks(
            String[] descriptions,
            TaskType[] types,
            String[] extraInfo,
            LocalDateTime[] deadlineDates,
            boolean[] isDone) {

        try {
            Files.createDirectories(DATA_FILE.getParent());

            if (!Files.exists(DATA_FILE)) {
                Files.createFile(DATA_FILE);
                return 0;
            }

            List<String> lines = Files.readAllLines(DATA_FILE);
            int taskCount = 0;

            for (String line : lines) {
                if (line.isBlank() || taskCount >= descriptions.length) {
                    continue;
                }

                String[] parts = line.split("\t", -1);

                if (parts.length != 4) {
                    continue;
                }

                try {
                    TaskType type = TaskType.valueOf(parts[0]);

                    types[taskCount] = type;
                    isDone[taskCount] = parts[1].equals("1");
                    descriptions[taskCount] = parts[2];

                    if (type == TaskType.DEADLINE) {
                        deadlineDates[taskCount] =
                                LocalDateTime.parse(parts[3]);
                        extraInfo[taskCount] = "";
                    } else {
                        deadlineDates[taskCount] = null;
                        extraInfo[taskCount] = parts[3];
                    }

                    taskCount++;

                } catch (IllegalArgumentException
                         | DateTimeParseException ignored) {
                    // Ignore corrupted entries.
                }
            }

            return taskCount;

        } catch (IOException e) {
            System.out.println("Unable to load saved tasks.");
            return 0;
        }
    }

    private static void saveTasks(
            String[] descriptions,
            TaskType[] types,
            String[] extraInfo,
            LocalDateTime[] deadlineDates,
            boolean[] isDone,
            int taskCount) {

        List<String> lines = new ArrayList<>();

        for (int i = 0; i < taskCount; i++) {
            String savedExtraInfo;

            if (types[i] == TaskType.DEADLINE) {
                savedExtraInfo = deadlineDates[i].toString();
            } else {
                savedExtraInfo = extraInfo[i];
            }

            lines.add(
                    types[i].name()
                            + "\t" + (isDone[i] ? "1" : "0")
                            + "\t" + descriptions[i]
                            + "\t" + savedExtraInfo
            );
        }

        try {
            Files.createDirectories(DATA_FILE.getParent());

            Files.write(
                    DATA_FILE,
                    lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

        } catch (IOException e) {
            System.out.println("Unable to save tasks.");
        }
    }

    private static String formatTask(
            int index,
            String[] descriptions,
            TaskType[] types,
            String[] extraInfo,
            LocalDateTime[] deadlineDates,
            boolean[] isDone) {

        String status = isDone[index] ? "X" : " ";

        if (types[index] == TaskType.TODO) {
            return "[T][" + status + "] " + descriptions[index];

        } else if (types[index] == TaskType.DEADLINE) {
            return "[D][" + status + "] "
                    + descriptions[index]
                    + " (by: "
                    + deadlineDates[index].format(OUTPUT_DATE_FORMAT)
                    + ")";

        } else {
            return "[E][" + status + "] "
                    + descriptions[index]
                    + " "
                    + extraInfo[index];
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String[] descriptions = new String[100];
        TaskType[] types = new TaskType[100];
        String[] extraInfo = new String[100];
        LocalDateTime[] deadlineDates = new LocalDateTime[100];
        boolean[] isDone = new boolean[100];

        int taskCount = loadTasks(
                descriptions,
                types,
                extraInfo,
                deadlineDates,
                isDone
        );

        System.out.println("Hello! I'm Nova.");
        System.out.println("What can I do for you?");

        while (true) {
            String input = scanner.nextLine();

            if (input.equals("bye")) {
                break;

            } else if (input.equals("list")) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(
                            (i + 1) + "."
                                    + formatTask(
                                    i,
                                    descriptions,
                                    types,
                                    extraInfo,
                                    deadlineDates,
                                    isDone
                            )
                    );
                }

            } else if (input.startsWith("mark ")) {
                int taskNumber =
                        Integer.parseInt(input.substring(5));

                int index = taskNumber - 1;

                if (index < 0 || index >= taskCount) {
                    System.out.println(
                            "That task number does not exist."
                    );

                } else {
                    isDone[index] = true;

                    saveTasks(
                            descriptions,
                            types,
                            extraInfo,
                            deadlineDates,
                            isDone,
                            taskCount
                    );

                    System.out.println(
                            "Nice! I've marked this task as done."
                    );
                }

            } else if (input.startsWith("delete ")) {
                int taskNumber =
                        Integer.parseInt(input.substring(7));

                int index = taskNumber - 1;

                if (index < 0 || index >= taskCount) {
                    System.out.println(
                            "That task number does not exist."
                    );

                } else {
                    System.out.println(
                            "Noted. I've removed this task:"
                    );

                    System.out.println(
                            formatTask(
                                    index,
                                    descriptions,
                                    types,
                                    extraInfo,
                                    deadlineDates,
                                    isDone
                            )
                    );

                    for (int i = index;
                         i < taskCount - 1;
                         i++) {

                        descriptions[i] =
                                descriptions[i + 1];

                        types[i] =
                                types[i + 1];

                        extraInfo[i] =
                                extraInfo[i + 1];

                        deadlineDates[i] =
                                deadlineDates[i + 1];

                        isDone[i] =
                                isDone[i + 1];
                    }

                    taskCount--;

                    saveTasks(
                            descriptions,
                            types,
                            extraInfo,
                            deadlineDates,
                            isDone,
                            taskCount
                    );

                    System.out.println(
                            "Now you have "
                                    + taskCount
                                    + " tasks in the list."
                    );
                }

            } else if (input.trim().equals("todo")) {
                System.out.println(
                        "Please provide a description for the todo."
                );

            } else if (input.startsWith("todo ")) {
                descriptions[taskCount] =
                        input.substring(5);

                types[taskCount] =
                        TaskType.TODO;

                extraInfo[taskCount] = "";
                deadlineDates[taskCount] = null;
                isDone[taskCount] = false;

                System.out.println(
                        "Got it. I've added this task:"
                );

                System.out.println(
                        formatTask(
                                taskCount,
                                descriptions,
                                types,
                                extraInfo,
                                deadlineDates,
                                isDone
                        )
                );

                taskCount++;

                saveTasks(
                        descriptions,
                        types,
                        extraInfo,
                        deadlineDates,
                        isDone,
                        taskCount
                );

                System.out.println(
                        "Now you have "
                                + taskCount
                                + " tasks in the list."
                );

            } else if (input.startsWith("deadline ")) {
                String content =
                        input.substring(9);

                String[] parts =
                        content.split(" /by ", 2);

                if (parts.length != 2) {
                    System.out.println(
                            "Please use: "
                                    + "deadline DESCRIPTION "
                                    + "/by yyyy-MM-dd HHmm"
                    );
                    continue;
                }

                try {
                    LocalDateTime deadline =
                            LocalDateTime.parse(
                                    parts[1],
                                    INPUT_DATE_FORMAT
                            );

                    descriptions[taskCount] =
                            parts[0];

                    types[taskCount] =
                            TaskType.DEADLINE;

                    deadlineDates[taskCount] =
                            deadline;

                    extraInfo[taskCount] = "";
                    isDone[taskCount] = false;

                    System.out.println(
                            "Got it. I've added this task:"
                    );

                    System.out.println(
                            formatTask(
                                    taskCount,
                                    descriptions,
                                    types,
                                    extraInfo,
                                    deadlineDates,
                                    isDone
                            )
                    );

                    taskCount++;

                    saveTasks(
                            descriptions,
                            types,
                            extraInfo,
                            deadlineDates,
                            isDone,
                            taskCount
                    );

                    System.out.println(
                            "Now you have "
                                    + taskCount
                                    + " tasks in the list."
                    );

                } catch (DateTimeParseException e) {
                    System.out.println(
                            "Please enter the deadline "
                                    + "as yyyy-MM-dd HHmm."
                    );
                }

            } else if (input.startsWith("event ")) {
                String content =
                        input.substring(6);

                String[] fromParts =
                        content.split(" /from ", 2);

                if (fromParts.length != 2) {
                    System.out.println(
                            "Please provide both "
                                    + "/from and /to."
                    );
                    continue;
                }

                String description =
                        fromParts[0];

                String[] toParts =
                        fromParts[1].split(" /to ", 2);

                if (toParts.length != 2) {
                    System.out.println(
                            "Please provide both "
                                    + "/from and /to."
                    );
                    continue;
                }

                String from = toParts[0];
                String to = toParts[1];

                descriptions[taskCount] =
                        description;

                types[taskCount] =
                        TaskType.EVENT;

                extraInfo[taskCount] =
                        "(from: "
                                + from
                                + " to: "
                                + to
                                + ")";

                deadlineDates[taskCount] = null;
                isDone[taskCount] = false;

                System.out.println(
                        "Got it. I've added this task:"
                );

                System.out.println(
                        formatTask(
                                taskCount,
                                descriptions,
                                types,
                                extraInfo,
                                deadlineDates,
                                isDone
                        )
                );

                taskCount++;

                saveTasks(
                        descriptions,
                        types,
                        extraInfo,
                        deadlineDates,
                        isDone,
                        taskCount
                );

                System.out.println(
                        "Now you have "
                                + taskCount
                                + " tasks in the list."
                );

            } else {
                System.out.println(
                        "Sorry, I don't understand that command."
                );
            }
        }

        System.out.println(
                "Bye. Hope to see you again soon!"
        );

        scanner.close();
    }
}