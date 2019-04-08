package loderunner.impl;

import loderunner.services.Coord;

public class CoordImpl implements Coord {
    private static int counter = 1;
    private int col, hgt;
    private int id;

    public CoordImpl(int col, int hgt) {
        this.col = col;
        this.hgt = hgt;
        this.id = counter; counter++;
    }

    public int getCol() { return col; }
    public int getHgt() { return hgt; }

    public void setCol(int col) { this.col = col; }
    public void setHgt(int hgt) { this.hgt = hgt; }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(this == o) return true;
        if(!(o instanceof Coord)) return false;
        Coord c2 = (Coord) o;
        if(c2.getCol() == getCol() && c2.getHgt() == getHgt()) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return 73 + 31 * id;
    }
}
