package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    public void parse_todoCommand_parsesDescription() {
        Parser.ParsedCommand command =
                Parser.parse("todo read book");

        assertEquals(
                Parser.CommandType.TODO,
                command.getType()
        );

        assertEquals(
                "read book",
                command.getDescription()
        );
    }

    @Test
    public void parse_deadlineCommand_parsesDateTime() {
        Parser.ParsedCommand command =
                Parser.parse(
                        "deadline return book /by 2019-12-02 1800"
                );

        assertEquals(
                Parser.CommandType.DEADLINE,
                command.getType()
        );

        assertEquals(
                "return book",
                command.getDescription()
        );

        assertEquals(
                LocalDateTime.of(
                        2019,
                        12,
                        2,
                        18,
                        0
                ),
                command.getDeadline()
        );
    }

    @Test
    public void parse_eventCommand_parsesTimes() {
        Parser.ParsedCommand command =
                Parser.parse(
                        "event meeting /from 2pm /to 4pm"
                );

        assertEquals(
                Parser.CommandType.EVENT,
                command.getType()
        );

        assertEquals(
                "meeting",
                command.getDescription()
        );

        assertEquals(
                "2pm",
                command.getFrom()
        );

        assertEquals(
                "4pm",
                command.getTo()
        );
    }

    @Test
    public void parse_markCommand_parsesTaskNumber() {
        Parser.ParsedCommand command =
                Parser.parse("mark 3");

        assertEquals(
                Parser.CommandType.MARK,
                command.getType()
        );

        assertEquals(
                3,
                command.getTaskNumber()
        );
    }

    @Test
    public void parse_invalidDeadline_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Parser.parse(
                        "deadline return book /by tomorrow"
                )
        );
    }

    @Test
    public void parse_missingTodoDescription_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Parser.parse("todo")
        );
    }

    @Test
    public void parse_invalidTaskNumber_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Parser.parse("delete abc")
        );
    }

    @Test
    public void parse_unknownCommand_returnsUnknown() {
        Parser.ParsedCommand command =
                Parser.parse("something random");

        assertEquals(
                Parser.CommandType.UNKNOWN,
                command.getType()
        );
    }
}