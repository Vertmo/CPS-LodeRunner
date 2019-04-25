package loderunner.impl;

import loderunner.services.Screen;
import loderunner.services.Cell;

public class ScreenImplBug implements Screen {
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
        cells[y][x] = Cell.PLT; // Oups !
    }

    @Override
    public void fill(int x, int y) {
        cells[y][x] = Cell.PLT;
        cells[0][0] = Cell.PLT; // Oups !
    }

    @Override
    public void triggerTrap(int x, int y) {
        cells[y][x] = Cell.HOL;
    }
}
