package loderunner.decorators;

import java.util.List;

import loderunner.services.Engine;
import loderunner.services.Game;
import loderunner.services.Level;

public abstract class GameDecorator implements Game {
    private Game delegate;

    public GameDecorator(Game delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Level> getLevels() {
        return delegate.getLevels();
    }

    @Override
    public Engine getEngine() {
        return delegate.getEngine();
    }

    @Override
    public int getLevelIndex() {
        return delegate.getLevelIndex();
    }

    @Override
    public int getScore() {
        return delegate.getScore();
    }

    @Override
    public int getHP() {
        return delegate.getHP();
    }

    @Override
    public void init(List<Level> levels) {
        delegate.init(levels);
    }

    @Override
    public void checkStateAndUpdate() {
        delegate.checkStateAndUpdate();
    }
}
