package loderunner.impl;

import loderunner.services.Hole;

public class HoleImpl implements Hole {
    private int col;
    private int hgt;
    private int t;

    public HoleImpl(int col, int hgt) {
        this.col = col;
        this.hgt = hgt;
    }

    @Override
    public int getCol() {
        return col;
    }

    @Override
    public int getHgt() {
        return hgt;
    }

    @Override
    public int getT() {
        return t;
    }

    @Override
    public void incT() {
        t++;
    }

    @Override
    public Hole clone() {
        HoleImpl h = new HoleImpl(col, hgt); h.t = t;
        return h;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(o == this) return true;
        if(!(o instanceof Hole)) return false;
        Hole h = (Hole) o;
        return h.getCol() == getCol() && h.getHgt() == getHgt();
    }

    @Override
    public int hashCode() {
        int iHashCode = 59;
        iHashCode = 31 * iHashCode + col;
        iHashCode = 31 * iHashCode + hgt;
        return iHashCode;
    }
}
