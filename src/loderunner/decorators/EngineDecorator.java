package loderunner.decorators;

import java.util.Set;

import loderunner.services.Command;
import loderunner.services.Coord;
import loderunner.services.EditableScreen;
import loderunner.services.Engine;
import loderunner.services.Environment;
import loderunner.services.Guard;
import loderunner.services.Hole;
import loderunner.services.Item;
import loderunner.services.Player;
import loderunner.services.Status;

public abstract class EngineDecorator implements Engine {
    private Engine delegate;

    public EngineDecorator(Engine delegate) {
        this.delegate = delegate;
    }

    @Override
    public Environment getEnvironment() {
        return delegate.getEnvironment();
    }

    @Override
    public Player getPlayer() {
        return delegate.getPlayer();
    }

    @Override
    public Set<Guard> getGuards() {
        return delegate.getGuards();
    }

    @Override
    public Set<Item> getTreasures() {
        return delegate.getTreasures();
    }

    @Override
    public Set<Hole> getHoles() {
        return delegate.getHoles();
    }

    @Override
    public Status getStatus() {
        return delegate.getStatus();
    }

    @Override
    public Command getNextCommand() {
        return delegate.getNextCommand();
    }

    @Override
    public void init(EditableScreen screen, Coord pCoord, Set<Coord> gCoords, Set<Coord> tCoords) {
        delegate.init(screen, pCoord, gCoords, tCoords);
    }

    @Override
    public void step() {
        delegate.step();
    }
}
