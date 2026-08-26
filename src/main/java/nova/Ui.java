package nova;

import java.util.Scanner;

public class Ui {

    private final Scanner scanner;

    public Ui() {
        scanner = new Scanner(System.in);
    }

    public void showWelcome() {
        System.out.println("Hello! I'm Nova.");
        System.out.println("What can I do for you?");
    }

    public String readCommand() {
        return scanner.nextLine();
    }

    public void showTaskList(TaskList tasks) {
        for (int i = 1; i <= tasks.size(); i++) {
            System.out.println(
                    i + "." + tasks.get(i).toDisplayString()
            );
        }
    }

    public void showAddedTask(
            Task task,
            int taskCount) {

        System.out.println(
                "Got it. I've added this task:"
        );

        System.out.println(task.toDisplayString());

        System.out.println(
                "Now you have "
                        + taskCount
                        + " tasks in the list."
        );
    }

    public void showDeletedTask(
            Task task,
            int taskCount) {

        System.out.println(
                "Noted. I've removed this task:"
        );

        System.out.println(task.toDisplayString());

        System.out.println(
                "Now you have "
                        + taskCount
                        + " tasks in the list."
        );
    }

    public void showMarked() {
        System.out.println(
                "Nice! I've marked this task as done."
        );
    }

    public void showError(String message) {
        System.out.println(message);
    }

    public void showBye() {
        System.out.println(
                "Bye. Hope to see you again soon!"
        );
    }

    public void close() {
        scanner.close();
    }
}