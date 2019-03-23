package loderunner.impl;

import loderunner.services.Coord;

public class CoordImpl implements Coord {
    private int col, hgt;

    public CoordImpl(int col, int hgt) {
        this.col = col;
        this.hgt = hgt;
    }

    public int getCol() { return col; }
    public int getHgt() { return hgt; }

    public void setCol(int col) { this.col = col; }
    public void setHgt(int hgt) { this.hgt = hgt; }
}
