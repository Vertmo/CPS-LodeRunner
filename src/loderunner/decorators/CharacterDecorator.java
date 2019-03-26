package loderunner.decorators;

import loderunner.services.Character;
import loderunner.services.Environment;

public abstract class CharacterDecorator implements Character {
    private Character delegate;

    public CharacterDecorator(Character delegate) {
        this.delegate = delegate;
    }

    @Override
    public Environment getEnvi() { return delegate.getEnvi(); }

    @Override
    public int getCol() { return delegate.getCol(); }

    @Override
    public int getHgt() { return delegate.getHgt(); }

    @Override
    public void init(Environment e, int x, int y) {
        delegate.init(e, x, y);
    }

    @Override
    public void goLeft() { delegate.goLeft(); }

    @Override
    public void goRight() { delegate.goRight(); }

    @Override
    public void goUp() { delegate.goUp(); }

    @Override
    public void goDown() { delegate.goDown(); }
}
