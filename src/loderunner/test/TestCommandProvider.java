package loderunner.test;

import java.util.List;

import loderunner.services.Command;
import loderunner.services.CommandProvider;

public class TestCommandProvider implements CommandProvider {
    private List<Command> commands;

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public Command getNextCommand() {
        return commands.remove(0);
    }

    @Override
    public Command peekNextCommand() {
        return commands.get(0);
    }
}
