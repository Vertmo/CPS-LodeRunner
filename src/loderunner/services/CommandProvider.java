package loderunner.services;

public interface CommandProvider {
    public Command getNextCommand();
    public Command peekNextCommand();
}
