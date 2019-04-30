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
import loderunner.services.PortalPair;
import loderunner.services.Status;

public abstract class EngineDecorator implements Engine {
    private Engine delegate;

    public EngineDecorator(Engine delegate) {
        this.delegate = delegate;
    }

    public Engine getDelegate() {
        return delegate;
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
    public Set<Item> getKeys() {
        return delegate.getKeys();
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
    public int getLevelScore() {
        return delegate.getLevelScore();
    }

    @Override
    public Command getNextCommand() {
        return delegate.getNextCommand();
    }

    @Override
    public Command peekNextCommand() {
        return delegate.peekNextCommand();
    }

    @Override
    public boolean isGuardTurn() {
        return delegate.isGuardTurn();
    }

    @Override
    public Set<PortalPair> getPortals() {
        return delegate.getPortals();
    }

    @Override
    public void init(EditableScreen screen, Coord pCoord, Set<Coord> gCoords, Set<Coord> tCoords, Set<Coord> kCoords, Set<PortalPair> portals) {
        delegate.init(screen, pCoord, gCoords, tCoords, kCoords, portals);
    }

    @Override
    public void step() {
        delegate.step();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public abstract Engine clone();

	public int getNumberBullets() {
		return delegate.getNumberBullets();
	}

	public void setNumberBullets(int nb) {
		delegate.setNumberBullets(nb);
	}
}
