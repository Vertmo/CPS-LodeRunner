package loderunner.impl;

import loderunner.services.Command;
import loderunner.services.CommandProvider;

public class NeutralCommandProvider implements CommandProvider {

    @Override
    public Command getNextCommand() {
        return Command.Neutral;
    }
}
