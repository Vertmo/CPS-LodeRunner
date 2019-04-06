package loderunner.windows;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import loderunner.services.Command;
import loderunner.services.CommandProvider;

public class KeyboardCommandProvider implements CommandProvider {
    private Command nextCmd;

    public KeyboardCommandProvider(Scene scene) {
        nextCmd = Command.Neutral;
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
                switch(key.getCode()) {
                    case LEFT:
                        nextCmd = Command.Left;
                        break;
                case RIGHT:
                    nextCmd = Command.Right;
                    break;
                case UP:
                    nextCmd = Command.Up;
                    break;
                case DOWN:
                    nextCmd = Command.Down;
                    break;
                    // TODO touches pour dig ? a et d ?
                default:
                    break;
                }
            });
    }

    @Override
    public Command getNextCommand() {
        Command currentCmd = nextCmd;
        nextCmd = Command.Neutral;
        return currentCmd;
    }
}
