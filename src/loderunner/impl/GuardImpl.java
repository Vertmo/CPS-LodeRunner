package loderunner.impl;

import loderunner.services.Cell;
import loderunner.services.Character;
import loderunner.services.Environment;
import loderunner.services.Guard;
import loderunner.services.InCell;
import loderunner.services.Move;

public class GuardImpl extends CharacterImpl implements Guard {
    private static int counter = 0;
    private int id;
    private int initCol, initHgt;
    private Character target;
    private int timeInHole;
    private boolean isShot;

    public GuardImpl() {
        id = counter; counter++;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getInitCol() {
        return initCol;
    }

    @Override
    public int getInitHgt() {
        return initHgt;
    }

    @Override
    public Move getBehaviour() {
        int hDist = Math.abs(getCol()-getTarget().getCol());
        int vDist = Math.abs(getHgt()-getTarget().getHgt());
        Cell cell = getEnvi().getCellNature(getCol(), getHgt());
        Cell cell_below = getEnvi().getCellNature(getCol(), getHgt()-1);
        Cell cell_left;
        if(getCol() == 0) cell_left = Cell.MTL;
        else cell_left = getEnvi().getCellNature(getCol()-1, getHgt());
        Cell cell_belowleft;
        if(getCol() == 0) cell_belowleft = Cell.MTL;
        else cell_belowleft = getEnvi().getCellNature(getCol()-1, getHgt()-1);
        Cell cell_right;
        if(getCol() == getEnvi().getWidth()-1) cell_right = Cell.MTL;
        else cell_right = getEnvi().getCellNature(getCol()+1, getHgt());
        Cell cell_belowright;
        if(getCol() == getEnvi().getWidth()-1) cell_belowright = Cell.MTL;
        else cell_belowright = getEnvi().getCellNature(getCol()+1, getHgt()-1);

        boolean guard_below = false;
        for(InCell ic: getEnvi().getCellContent(getCol(), getHgt()-1)) {
            if(ic instanceof Guard) guard_below = true;
        }

        if(cell_below == Cell.LAD && !guard_below
           && getHgt() > getTarget().getHgt() && (hDist > vDist || hDist == 0)) {
            return Move.Down;
        }
        if(cell == Cell.HDR && (vDist < hDist || hDist == 0) && getHgt() > getTarget().getHgt()) return Move.Down;
        if(cell == Cell.LAD) {
            if((cell_below == Cell.PLT || cell_below == Cell.MTL || guard_below || cell_left == Cell.HDR || cell_belowleft == Cell.PLT || cell_belowleft == Cell.MTL)
               && (hDist < vDist || vDist == 0) && getTarget().getCol() < getCol()) return Move.Left;
            if((cell_below == Cell.PLT || cell_below == Cell.MTL || guard_below || cell_right == Cell.HDR || cell_belowright == Cell.PLT || cell_belowright == Cell.MTL)
               && (hDist < vDist || vDist == 0) && getTarget().getCol() > getCol()) return Move.Right;
            if(getTarget().getHgt() < getHgt()) return Move.Down;
            if(cell == Cell.LAD && getTarget().getHgt() > getHgt()) return Move.Up;
            return Move.Neutral;
        }
        if(cell_below == Cell.PLT || cell_below == Cell.MTL || cell_below == Cell.LAD
           || cell == Cell.HDR || cell == Cell.HOL || guard_below) {
            if(getTarget().getCol() < getCol()) return Move.Left;
            if(getTarget().getCol() > getCol()) return Move.Right;
            return Move.Neutral;
        }
        return Move.Neutral;
    }

    @Override
    public Character getTarget() {
        if(target == null) return this;
        return target;
    }

    @Override
    public int getTimeInHole() {
        return timeInHole;
    }

    @Override
    public boolean isShot() {
        return isShot;
    }

    @Override
    public void init(Environment e, Character t, int x, int y) {
        super.init(e, x, y);
        initCol = x; initHgt = y;
        target = t;
        timeInHole = 0;
        isShot = false;
    }

    @Override
    public void climbLeft() {
        if(getCol() <= 0) return;
        Cell nat_arrivee = getEnvi().getCellNature(getCol()-1, getHgt()+1);
        if(nat_arrivee == Cell.PLT || nat_arrivee == Cell.MTL) return;
        for(InCell ic: getEnvi().getCellContent(getCol()-1, getHgt()+1)) {
            if(ic instanceof Guard) return;
        }
        col = col-1; hgt = hgt+1; timeInHole = 0;
    }

    @Override
    public void climbRight() {
        if(getCol() >= getEnvi().getWidth()-1) return;
        Cell nat_arrivee = getEnvi().getCellNature(getCol()+1, getHgt()+1);
        if(nat_arrivee == Cell.PLT || nat_arrivee == Cell.MTL) return;
        for(InCell ic: getEnvi().getCellContent(getCol()+1, getHgt()+1)) {
            if(ic instanceof Guard) return;
        }
        col = col+1; hgt = hgt+1; timeInHole = 0;
    }

    private boolean willFall() {
        Cell cell_below = getEnvi().getCellNature(getCol(), getHgt()-1);
        if(cell_below != Cell.HOL && cell_below != Cell.EMP && cell_below != Cell.HDR) return false;
        for(InCell ic: getEnvi().getCellContent(getCol(), getHgt()-1)) {
            if(ic instanceof Guard) return false;
        }
        Cell c = getEnvi().getCellNature(getCol(), getHgt());
        if(c == Cell.LAD || c == Cell.HDR) return false;
        return true;
    }

    @Override
    public void step() {
        if(isShot()) {
            col = initCol;
            hgt = initHgt;
            timeInHole = 0;
        }
        else if(willFall()) goDown();
        else if(getEnvi().getCellNature(getCol(), getHgt()) == Cell.HOL) {
            if(getTimeInHole() < 5) timeInHole++;
            else {
                if(getBehaviour() == Move.Left) climbLeft();
                else if(getBehaviour() == Move.Right) climbRight();
            }
        } else {
            if(getBehaviour() == Move.Left) goLeft();
            else if(getBehaviour() == Move.Right) goRight();
            else if(getBehaviour() == Move.Up) goUp();
            else if(getBehaviour() == Move.Down) goDown();
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(o == this) return true;
        if(!(o instanceof Guard)) return false;
        Guard g = (Guard) o;
        return g.getTarget().equals(getTarget())
            && g.getCol() == getCol() && g.getHgt() == getHgt()
            && g.getInitCol() == getInitCol() && g.getInitHgt() == getInitHgt()
            && g.getTimeInHole() == getTimeInHole();
    }

    @Override
    public int hashCode() {
        return 31 + 27 * getId();
    }

    @Override
    public Guard clone() {
        GuardImpl g = new GuardImpl();
        g.id = getId();
        g.init(getEnvi(), getTarget(), getInitCol(), getInitHgt());
        g.col = getCol(); g.hgt = getHgt(); g.timeInHole = getTimeInHole();
        return g;
    }

    @Override
    public void setIsShot(boolean b) {
        isShot = b;
    }
}
