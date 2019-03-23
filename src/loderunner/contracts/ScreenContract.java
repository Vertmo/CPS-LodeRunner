package loderunner.contracts;

import loderunner.services.Cell;
import loderunner.services.Screen;
import loderunner.contracts.errors.PostconditionError;
import loderunner.contracts.errors.PreconditionError;
import loderunner.decorators.ScreenDecorator;

public class ScreenContract extends ScreenDecorator {
    public ScreenContract(Screen delegate) {
        super(delegate);
    }

    public void checkInvariant() {
        // Y'en a pas !
    }

    @Override
    public Cell getCellNature(int x, int y) {
        // pre: 0 <= x < getWidth() && 0 <= y < getHeight()
        if(x < 0 || x >= getWidth() || y < 0 || y >= getHeight())
            throw new PreconditionError("Screen", "getCellNature", "0 <= x < getWidth() && 0 <= y < getHeight()");

        // run
        return super.getCellNature(x, y);
    }

    @Override
    public void init(int w, int h) {
        // pre: 0 < w && 0 < h
        if(w <= 0 || h <= 0) throw new PreconditionError("Screen", "init", "0 < w && 0 < h");

        // run
        super.init(w, h);

        // post-invariant
        checkInvariant();

        // post: getWidth() == w
        if(getWidth() != w) throw new PostconditionError("Screen", "init", "getWidth() == w");

        // post: getHeight() == h
        if(getHeight() != h) throw new PostconditionError("Screen", "init", "getHeight() == h");

        // post: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[ getCellNature(x, y) == EMP
        for(int x = 0; x < getWidth(); x++) {
            for(int y = 0; y < getHeight(); y++) {
                if(getCellNature(x, y) != Cell.EMP)
                    throw new PostconditionError("Screen", "init", "One of the cell is not EMP");
            }
        }
    }

    @Override
    public void dig(int x, int y) {
        // pre: getCellNature(x, y) == PLT
        if(getCellNature(x, y) != Cell.PLT)
            throw new PreconditionError("Screen", "dig", "getCellNature(x, y) == PLT");

        // captures
        Cell[][] cellNature_pre = new Cell[getWidth()][getHeight()];
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                cellNature_pre[i][j] = getCellNature(i, j);
            }
        }

        // pre-invariant
        checkInvariant();

        // run
        super.dig(x, y);

        // post-invariant
        checkInvariant();

        // post: getCellNature(x, y) == HOL
        if(getCellNature(x, y) != Cell.HOL)
            throw new PostconditionError("Screen", "dig", "getCellNature(x, y) == HOL");

        // post: \forall i \in [0..getWidth()[ \forall j \in [0..getHeight()[
        //         (i != x || j != y) => getCellNature(i, j) == getCellNature(i, j)@pre
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                if((i != x || j != y) && getCellNature(i, j) != cellNature_pre[i][j])
                    throw new PostconditionError("Screen", "dig", "One of the other cells has changed");
            }
        }
    }

    @Override
    public void fill(int x, int y) {
        // pre: getCellNature(x, y) == HOL
        if(getCellNature(x, y) != Cell.HOL)
            throw new PreconditionError("Screen", "dig", "getCellNature(x, y) == HOL");

        // captures
        Cell[][] cellNature_pre = new Cell[getWidth()][getHeight()];
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                cellNature_pre[i][j] = getCellNature(i, j);
            }
        }

        // pre-invariant
        checkInvariant();

        // run
        super.fill(x, y);

        // post-invariant
        checkInvariant();

        // post: getCellNature(x, y) == PLT
        if(getCellNature(x, y) != Cell.PLT)
            throw new PostconditionError("Screen", "fill", "getCellNature(x, y) == PLT");

        // post: \forall i \in [0..getWidth()[ \forall j \in [0..getHeight()[
        //         (i != x || j != y) => getCellNature(i, j) == getCellNature(i, j)@pre
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                if((i != x || j != y) && getCellNature(i, j) != cellNature_pre[i][j])
                    throw new PostconditionError("Screen", "fill", "One of the other cells has changed");
            }
        }
    }
}
