package loderunner.impl;

import loderunner.services.Screen;
import loderunner.services.Cell;

public class ScreenImpl implements Screen {
    protected Cell[][] cells;

    @Override
    public void init(int w, int h) {
        cells = new Cell[h][w];
        for(int i = 0; i < h; i++) {
            for(int j = 0; j < w; j++) cells[i][j] = Cell.EMP;
        }
    }

    @Override
    public int getWidth() {
        return cells[0].length;
    }

    @Override
    public int getHeight() {
        return cells.length;
    }

    @Override
    public Cell getCellNature(int x, int y) {
        return cells[y][x];
    }

    @Override
    public void dig(int x, int y) {
        cells[y][x] = Cell.HOL;
    }

    @Override
    public void fill(int x, int y) {
        cells[y][x] = Cell.PLT;
    }

    @Override
    public void triggerTrap(int x, int y) {
        cells[y][x] = Cell.EMP;
    }
}
