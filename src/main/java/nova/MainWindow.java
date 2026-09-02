package nova;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Controls Nova's main conversation window.
 */
public class MainWindow {

    private Nova nova;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Connects the window to Nova and displays its initial greeting.
     *
     * @param nova Nova instance that processes user commands.
     */
    public void setNova(Nova nova) {
        this.nova = nova;
        dialogContainer.getChildren().add(
                DialogBox.getNovaDialog(nova.getWelcomeMessage()));
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = nova.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getNovaDialog(response));
        userInput.clear();
    }
}
