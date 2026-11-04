import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.junit.jupiter.api.Test;

class InputControllerTest {
    @Test
    void translatesSupportedKeysIntoCommands() {
        List<InputController.Command> commands = new ArrayList<>();
        InputController controller = new InputController(commands::add);
        JPanel source = new JPanel();

        press(controller, source, KeyEvent.VK_UP);
        press(controller, source, KeyEvent.VK_DOWN);
        press(controller, source, KeyEvent.VK_LEFT);
        press(controller, source, KeyEvent.VK_RIGHT);
        press(controller, source, KeyEvent.VK_SPACE);
        press(controller, source, KeyEvent.VK_P);
        press(controller, source, KeyEvent.VK_R);
        press(controller, source, KeyEvent.VK_M);

        assertEquals(List.of(
                InputController.Command.MOVE_UP,
                InputController.Command.MOVE_DOWN,
                InputController.Command.MOVE_LEFT,
                InputController.Command.MOVE_RIGHT,
                InputController.Command.START,
                InputController.Command.PAUSE,
                InputController.Command.RESTART,
                InputController.Command.TOGGLE_MUTE), commands);
    }

    @Test
    void ignoresUnsupportedKeys() {
        List<InputController.Command> commands = new ArrayList<>();
        InputController controller = new InputController(commands::add);

        press(controller, new JPanel(), KeyEvent.VK_A);

        assertTrue(commands.isEmpty());
    }

    private void press(InputController controller, JPanel source, int keyCode) {
        controller.keyPressed(new KeyEvent(source, KeyEvent.KEY_PRESSED,
                1L, 0, keyCode, KeyEvent.CHAR_UNDEFINED));
    }
}
