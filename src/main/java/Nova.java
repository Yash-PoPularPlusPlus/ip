import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Nova {

    private static final Path DATA_FILE = Path.of("data", "nova.txt");

    private enum TaskType {
        TODO,
        DEADLINE,
        EVENT
    }

    private static int loadTasks(
            String[] descriptions,
            TaskType[] types,
            String[] extraInfo,
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
                    types[taskCount] = TaskType.valueOf(parts[0]);
                    isDone[taskCount] = parts[1].equals("1");
                    descriptions[taskCount] = parts[2];
                    extraInfo[taskCount] = parts[3];
                    taskCount++;
                } catch (IllegalArgumentException ignored) {
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
            boolean[] isDone,
            int taskCount) {

        List<String> lines = new ArrayList<>();

        for (int i = 0; i < taskCount; i++) {
            lines.add(
                    types[i].name()
                            + "\t" + (isDone[i] ? "1" : "0")
                            + "\t" + descriptions[i]
                            + "\t" + extraInfo[i]
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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String[] descriptions = new String[100];
        TaskType[] types = new TaskType[100];
        String[] extraInfo = new String[100];
        boolean[] isDone = new boolean[100];

        int taskCount = loadTasks(
                descriptions,
                types,
                extraInfo,
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
                    String status = isDone[i] ? "X" : " ";

                    if (types[i] == TaskType.TODO) {
                        System.out.println(
                                (i + 1) + ".[T][" + status + "] "
                                        + descriptions[i]
                        );

                    } else if (types[i] == TaskType.DEADLINE) {
                        System.out.println(
                                (i + 1) + ".[D][" + status + "] "
                                        + descriptions[i]
                                        + " (by: " + extraInfo[i] + ")"
                        );

                    } else if (types[i] == TaskType.EVENT) {
                        System.out.println(
                                (i + 1) + ".[E][" + status + "] "
                                        + descriptions[i]
                                        + " " + extraInfo[i]
                        );
                    }
                }

            } else if (input.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(input.substring(5));
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
                            isDone,
                            taskCount
                    );

                    System.out.println(
                            "Nice! I've marked this task as done."
                    );
                }

            } else if (input.startsWith("delete ")) {
                int taskNumber = Integer.parseInt(input.substring(7));
                int index = taskNumber - 1;

                if (index < 0 || index >= taskCount) {
                    System.out.println(
                            "That task number does not exist."
                    );

                } else {
                    String status = isDone[index] ? "X" : " ";

                    System.out.println(
                            "Noted. I've removed this task:"
                    );

                    if (types[index] == TaskType.TODO) {
                        System.out.println(
                                "[T][" + status + "] "
                                        + descriptions[index]
                        );

                    } else if (types[index] == TaskType.DEADLINE) {
                        System.out.println(
                                "[D][" + status + "] "
                                        + descriptions[index]
                                        + " (by: " + extraInfo[index] + ")"
                        );

                    } else if (types[index] == TaskType.EVENT) {
                        System.out.println(
                                "[E][" + status + "] "
                                        + descriptions[index]
                                        + " " + extraInfo[index]
                        );
                    }

                    for (int i = index; i < taskCount - 1; i++) {
                        descriptions[i] = descriptions[i + 1];
                        types[i] = types[i + 1];
                        extraInfo[i] = extraInfo[i + 1];
                        isDone[i] = isDone[i + 1];
                    }

                    taskCount--;

                    saveTasks(
                            descriptions,
                            types,
                            extraInfo,
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
                descriptions[taskCount] = input.substring(5);
                types[taskCount] = TaskType.TODO;
                extraInfo[taskCount] = "";
                isDone[taskCount] = false;

                System.out.println(
                        "Got it. I've added this task:"
                );

                System.out.println(
                        "[T][ ] " + descriptions[taskCount]
                );

                taskCount++;

                saveTasks(
                        descriptions,
                        types,
                        extraInfo,
                        isDone,
                        taskCount
                );

                System.out.println(
                        "Now you have "
                                + taskCount
                                + " tasks in the list."
                );

            } else if (input.startsWith("deadline ")) {
                String content = input.substring(9);
                String[] parts = content.split(" /by ", 2);

                descriptions[taskCount] = parts[0];
                types[taskCount] = TaskType.DEADLINE;
                extraInfo[taskCount] = parts[1];
                isDone[taskCount] = false;

                System.out.println(
                        "Got it. I've added this task:"
                );

                System.out.println(
                        "[D][ ] "
                                + descriptions[taskCount]
                                + " (by: "
                                + extraInfo[taskCount]
                                + ")"
                );

                taskCount++;

                saveTasks(
                        descriptions,
                        types,
                        extraInfo,
                        isDone,
                        taskCount
                );

                System.out.println(
                        "Now you have "
                                + taskCount
                                + " tasks in the list."
                );

            } else if (input.startsWith("event ")) {
                String content = input.substring(6);

                String[] fromParts =
                        content.split(" /from ", 2);

                String description = fromParts[0];

                String[] toParts =
                        fromParts[1].split(" /to ", 2);

                String from = toParts[0];
                String to = toParts[1];

                descriptions[taskCount] = description;
                types[taskCount] = TaskType.EVENT;

                extraInfo[taskCount] =
                        "(from: " + from + " to: " + to + ")";

                isDone[taskCount] = false;

                System.out.println(
                        "Got it. I've added this task:"
                );

                System.out.println(
                        "[E][ ] "
                                + descriptions[taskCount]
                                + " "
                                + extraInfo[taskCount]
                );

                taskCount++;

                saveTasks(
                        descriptions,
                        types,
                        extraInfo,
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