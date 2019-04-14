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
        int iHashCode = 17;
        iHashCode = 31 * iHashCode + col;
        iHashCode = 31 * iHashCode + hgt;
        return iHashCode;
    }
}
