package loderunner.windows;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import loderunner.services.Command;
import loderunner.services.CommandProvider;

public class KeyboardCommandProviderV2 implements CommandProvider {
    private Command nextCmd;

    public KeyboardCommandProviderV2(Scene scene) {
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
            case E:
                nextCmd = Command.DigL;
                break;
            case R:
                nextCmd = Command.DigR;
                break;
            case D:
                nextCmd = Command.ShootL;
                break;
            case F:
                nextCmd = Command.ShootR;
                break;
            default:
                break;
            }
        });
        scene.addEventHandler(KeyEvent.KEY_RELEASED, (key) -> {
            switch(key.getCode()) {
            case LEFT:
                if(nextCmd == Command.Left)
                    nextCmd = Command.Neutral;
                break;
            case RIGHT:
                if(nextCmd == Command.Right)
                    nextCmd = Command.Neutral;
                break;
            case UP:
                if(nextCmd == Command.Up)
                    nextCmd = Command.Neutral;
                break;
            case DOWN:
                if(nextCmd == Command.Down)
                    nextCmd = Command.Neutral;
                break;
            default:
                break;
            }
        });
    }

    @Override
    public Command getNextCommand() {
        Command currentCmd = nextCmd;
        if(currentCmd == Command.DigL || currentCmd == Command.DigR ||
                currentCmd == Command.ShootL || currentCmd == Command.ShootR)
            nextCmd = Command.Neutral;
        return currentCmd;
    }

    @Override
    public Command peekNextCommand() {
        return nextCmd;
    }
}
