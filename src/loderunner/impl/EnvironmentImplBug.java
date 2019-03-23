package loderunner.impl;

import java.util.HashSet;
import java.util.Set;

import loderunner.services.Cell;
import loderunner.services.Environment;
import loderunner.services.InCell;
import loderunner.services.Screen;

public class EnvironmentImplBug extends ScreenImpl implements Environment {
    private Set<InCell>[][] cellContents;

    @Override
    public Set<InCell> getCellContent(int x, int y) {
        return cellContents[y][x];
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(int w, int h) {
        super.init(w, h);

        cellContents = (Set<InCell>[][]) new Set[h][w];
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) cellContents[y][x] = new HashSet<InCell>();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(Screen s) {
        cells = new Cell[s.getHeight()][s.getWidth()];
        for(int y = 0; y < s.getHeight(); y++) {
            for(int x = 0; x < s.getWidth(); x++) cells[y][x] = s.getCellNature(x, y);
        }

        cellContents = (Set<InCell>[][]) new Set[s.getHeight()][s.getWidth()];
        for(int y = 0; y < s.getHeight(); y++) {
            for(int x = 0; x < s.getWidth(); x++) cellContents[y][x] = new HashSet<InCell>();
        }
    }

    @Override
    public void addCellContent(int x, int y, InCell c) {
        cellContents[0][x].add(c); // Oups
    }

    @Override
    public void removeCellContent(int x, int y, InCell c) {
        cellContents[y][0].remove(c); // Oups
    }
}
