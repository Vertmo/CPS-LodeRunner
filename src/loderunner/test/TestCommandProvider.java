package loderunner.test;

import java.util.List;

import loderunner.services.Command;
import loderunner.services.CommandProvider;

public class TestCommandProvider implements CommandProvider {
    private List<Command> commands;

    @Override
    public Command getNextCommand() {
        return commands.remove(0);
    }
}
