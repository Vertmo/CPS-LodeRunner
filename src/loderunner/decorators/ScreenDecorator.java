package loderunner.decorators;

import loderunner.services.Screen;
import loderunner.services.Cell;

public abstract class ScreenDecorator implements Screen {
    private final Screen delegate;

    public ScreenDecorator(Screen delegate) {
        this.delegate = delegate;
    }

    @Override
    public int getWidth() {
        return delegate.getWidth();
    }

    @Override
    public int getHeight() {
        return delegate.getHeight();
    }

    @Override
    public Cell getCellNature(int x, int y) {
        return delegate.getCellNature(x, y);
    }

    @Override
    public void init(int w, int h) {
        delegate.init(w, h);
    }

    @Override
    public void dig(int x, int y) {
        delegate.dig(x, y);
    }

    @Override
    public void fill(int x, int y) {
        delegate.fill(x, y);
    }

    @Override
    public void triggerTrap(int x, int y) {
        delegate.triggerTrap(x, y);
    }

    @Override
    public void openDoor(int x, int y) {
        delegate.openDoor(x, y);
    }
}
