package loderunner.impl;

import loderunner.services.Cell;
import loderunner.services.Character;
import loderunner.services.Environment;
import loderunner.services.Guard;
import loderunner.services.InCell;

public class CharacterImplBug implements Character {
    private Environment envi;
    private int col, hgt;

    @Override
    public Environment getEnvi() {
        return envi;
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
    public void init(Environment e, int x, int y) {
        envi = e;
        col = x; hgt = y;
    }

    @Override
    public void goLeft() {
        if(col == 0) return;
        if(envi.getCellNature(col-1, hgt) == Cell.PLT || envi.getCellNature(col-1, hgt) == Cell.MTL) return;
        // boolean charUnderneath = false;
        // for(InCell ic: envi.getCellContent(col, hgt-1)) {
        //     if (ic instanceof Character) charUnderneath = true;
        // }
        // if(envi.getCellNature(col, hgt) != Cell.LAD && envi.getCellNature(col, hgt) != Cell.HDR
        //    && envi.getCellNature(col, hgt-1) != Cell.PLT && envi.getCellNature(col, hgt-1) != Cell.MTL
        //    && !charUnderneath) return; // Oups
        boolean guardLeft = false;
        for(InCell ic: envi.getCellContent(col-1, hgt)) {
            if (ic instanceof Guard) guardLeft = true;
        }
        if(this instanceof Guard && guardLeft) return;
        col--;
    }

    @Override
    public void goRight() {
        if(col == envi.getWidth()-1) return;
        if(envi.getCellNature(col+1, hgt) == Cell.PLT || envi.getCellNature(col+1, hgt) == Cell.MTL) return;
        boolean charUnderneath = false;
        for(InCell ic: envi.getCellContent(col, hgt-1)) {
            if (ic instanceof Character) charUnderneath = true;
        }
        if(envi.getCellNature(col, hgt) != Cell.LAD && envi.getCellNature(col, hgt) != Cell.HDR
           && envi.getCellNature(col, hgt-1) != Cell.PLT && envi.getCellNature(col, hgt-1) != Cell.MTL
           && !charUnderneath) return;
        boolean guardRight = false;
        for(InCell ic: envi.getCellContent(col+1, hgt)) {
            if (ic instanceof Guard) guardRight = true;
        }
        if(this instanceof Guard && guardRight) return;
        col--; // Oups
    }

    @Override
    public void goUp() {
        if(hgt == envi.getHeight()-1) return;
        if(envi.getCellNature(col, hgt) == Cell.LAD) {
            Cell upNature = envi.getCellNature(col, hgt+1);
            boolean guardUp = false;
            for(InCell ic: envi.getCellContent(col, hgt+1)) {
                if (ic instanceof Guard) guardUp = false; // Oups
            }
            if((upNature == Cell.EMP || upNature == Cell.HOL || upNature == Cell.LAD || upNature == Cell.HDR)
               && (!(this instanceof Guard) || !guardUp)) hgt++;
        }
    }

    @Override
    public void goDown() {
        if(hgt == 0) return;
        Cell downNature = envi.getCellNature(col, hgt+1); // Oups
        boolean guardDown = false;
        for(InCell ic: envi.getCellContent(col, hgt-1)) {
            if (ic instanceof Guard) guardDown = true;
        }
        if((downNature == Cell.EMP || downNature == Cell.HOL
            || downNature == Cell.LAD || downNature == Cell.HDR)
           && !guardDown) hgt--;
    }
}
