import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

final class InputController extends KeyAdapter {
    enum Command {
        MOVE_UP,
        MOVE_DOWN,
        MOVE_LEFT,
        MOVE_RIGHT,
        START,
        PAUSE,
        RESTART,
        TOGGLE_MUTE
    }

    private final Consumer<Command> commandHandler;

    InputController(Consumer<Command> commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        Command command = commandFor(event.getKeyCode());
        if (command != null) {
            commandHandler.accept(command);
        }
    }

    private Command commandFor(int keyCode) {
        if (keyCode == KeyEvent.VK_UP) return Command.MOVE_UP;
        if (keyCode == KeyEvent.VK_DOWN) return Command.MOVE_DOWN;
        if (keyCode == KeyEvent.VK_LEFT) return Command.MOVE_LEFT;
        if (keyCode == KeyEvent.VK_RIGHT) return Command.MOVE_RIGHT;
        if (keyCode == KeyEvent.VK_SPACE) return Command.START;
        if (keyCode == KeyEvent.VK_P) return Command.PAUSE;
        if (keyCode == KeyEvent.VK_R) return Command.RESTART;
        if (keyCode == KeyEvent.VK_M) return Command.TOGGLE_MUTE;
        return null;
    }
}
