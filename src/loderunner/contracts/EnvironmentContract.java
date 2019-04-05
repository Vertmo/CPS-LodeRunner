package loderunner.contracts;

import java.util.HashSet;
import java.util.Set;

import loderunner.contracts.errors.InvariantError;
import loderunner.contracts.errors.PostconditionError;
import loderunner.contracts.errors.PreconditionError;
import loderunner.services.Cell;
import loderunner.services.Environment;
import loderunner.services.Guard;
import loderunner.services.InCell;
import loderunner.services.Item;
import loderunner.services.ItemType;
import loderunner.services.Screen;

public class EnvironmentContract extends ScreenContract implements Environment {
    private final Environment delegate;

    public EnvironmentContract(Environment delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public void checkInvariant() {
        super.checkInvariant();

        // inv: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[
        //        \forall Guard g1 \in getCellContent(x, y)
        //          \forall Guard g2 \in getCellContent(x, y)
        //            g1 == g2
        for(int x = 0; x < getWidth(); x++) {
            for(int y = 0; y < getHeight(); y++) {
                for(InCell ic1: getCellContent(x, y)) {
                    for(InCell ic2: getCellContent(x, y)) {
                        if(ic1 instanceof Guard && ic2 instanceof Guard && ic1 != ic2)
                            throw new InvariantError("Environment", "There are two different guards in the same cell");
                    }
                }
            }
        }

        // inv: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[
        //        getCellNature(x, y) \in { PLT, MTL } => getCellContent(x, y).isEmpty()
        for(int x = 0; x < getWidth(); x++) {
            for(int y = 0; y < getHeight(); y++) {
                if((getCellNature(x, y) == Cell.PLT || getCellNature(x, y) == Cell.MTL)
                   && !getCellContent(x, y).isEmpty())
                    throw new InvariantError("Environment", "A PLT or MTL cell is not empty");
            }
        }

        // inv: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[
        //        \exists Treasure t \in getCellContent(x, y) =>
        //        (getCellNature(x, y) == EMP && getCellNature(x, y-1) \in { PLT, MTL })
        for(int x = 0; x < getWidth(); x++) {
            for(int y = 0; y < getHeight(); y++) {
                for(InCell ic: getCellContent(x, y)) {
                    if(ic instanceof Item && ((Item)ic).getNature()==ItemType.Treasure
                       && (getCellNature(x, y) != Cell.EMP
                           || !(getCellNature(x, y-1) == Cell.PLT || getCellNature(x, y-1) == Cell.MTL)))
                        throw new InvariantError("Environment", "A Treasure is on an invalid cell");
                }
            }
        }
    }

    @Override
    public Set<InCell> getCellContent(int x, int y) {
        // pre: 0 <= x < getWidth() && 0 <= y < getHeight()
        if(x < 0 || x >= getWidth() || y < 0 || y >= getHeight())
            throw new PreconditionError("Environment", "getCellContent", "0 <= x < getWidth() && 0 <= y < getHeight()");

        // run
        return delegate.getCellContent(x, y);
    }

    @Override
    public void init(int w, int h) {
        super.init(w, h);

        // post: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[
        //         getCellContent(x, y).isEmpty()
        for(int x = 0; x < getWidth(); x++) {
            for(int y = 0; y < getHeight(); y++) {
                if(!getCellContent(x, y).isEmpty())
                    throw new PostconditionError("Environment", "init", "One of the cell is not empty");
            }
        }
    }

    @Override
    public void init(Screen s) {
        // run
        delegate.init(s);

        // post-invariant
        checkInvariant();

        // post: getWidth() == s.getWidth()
        if(getWidth() != s.getWidth())
            throw new PostconditionError("Environment", "init", "getWidth() == s.getWidth()");

        // post: getHeight() == s.getHeight()
        if(getHeight() != s.getHeight())
            throw new PostconditionError("Environment", "init", "getHeight() == s.getHeight()");

        // post: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[
        //         getCellNature(x, y) == s.getCellNature(x, y)
        for(int x = 0; x < getWidth(); x++) {
            for(int y = 0; y < getHeight(); y++) {
                if(getCellNature(x, y) != s.getCellNature(x, y))
                    throw new PostconditionError("Environment", "init", "One of the cell has not been copied properly");
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addCellContent(int x, int y, InCell c) {
        // pre: 0 <= x < getWidth() && 0 <= y < getHeight()
        if(x < 0 || x >= getWidth() || y < 0 || y >= getHeight())
            throw new PreconditionError("Environment", "addCellContent", "0 <= x < getWidth() && 0 <= y < getHeight()");

        // pre: getCellNature(x, y) == EMP && getCellNature(x, y-1) \in { PLT, MTL }
        if(getCellNature(x, y) != Cell.EMP ||
           (getCellNature(x, y-1) != Cell.PLT && getCellNature(x, y-1) != Cell.MTL))
            throw new PreconditionError("Environment", "addCellContent", "The cell is not empty or is not above a solid cell");

        // captures
        int width_pre = getWidth();
        int height_pre = getHeight();
        Cell[][] cellNature_pre = new Cell[getWidth()][getHeight()];
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                cellNature_pre[i][j] = getCellNature(i, j);
            }
        }

        Set<InCell>[][] cellContent_pre = (Set<InCell>[][]) new Set[getWidth()][getHeight()];
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                cellContent_pre[i][j] = new HashSet<>(getCellContent(i, j));
            }
        }

        // pre-invariant
        checkInvariant();

        // run
        delegate.addCellContent(x, y, c);

        // post-invariant
        checkInvariant();

        // const: getWidth()
        if(getWidth() != width_pre)
            throw new PostconditionError("Environment", "addCellContent", "getWidth() is supposed to be constant");
        // const: getHeight()
        if(getHeight() != height_pre)
            throw new PostconditionError("Environment", "addCellContent", "getHeight() is supposed to be constant");

        // post: c \in getCellContent(x, y)
        if(!(getCellContent(x, y).contains(c)))
            throw new PostconditionError("Environment", "addCellContent", "c in getCellContent(x, y)");

        // post: \forall InCell c2 \in getCellContent(x, y)@pre c2 \in getCellContent(x, y)
        //       && \forall InCell c2 \in getCellContent(x, y) c2 != c => c2 \in getCellContent(x, y)@pre
        for(InCell c2: cellContent_pre[x][y]) {
            if(!(getCellContent(x, y).contains(c2)))
                throw new PostconditionError("Environment", "addCellContent", "The content of the cell has changed unexpectedly");
        }
        for(InCell c2: getCellContent(x, y)) {
            if(c != c2 && !cellContent_pre[x][y].contains(c2))
                throw new PostconditionError("Environment", "addCellContent", "The content of the cell has changed unexpectedly");
        }

        // post: \forall i \in [0..getWidth()[ \forall j \in [0..getHeight()[
        //         (i != x || j != y) => getCellContent(i, j) == getCellContent(i, j)@pre
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                if((i != x || j != y) && !getCellContent(i, j).equals(cellContent_pre[i][j]))
                    throw new PostconditionError("Environment", "addCellContent", "The content of another cell has changed");
            }
        }

        // post: \forall i \in [0..getWidth()[ \forall j \in [0..getHeight()[
        //         getCellNature(x, y) == getCellNature(x, y)@pre
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                if(getCellNature(i, j) != cellNature_pre[i][j])
                    throw new PostconditionError("Environment", "addCellContent", "The nature of a cell has changed");
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void removeCellContent(int x, int y, InCell c) {
        // pre: c \in getCellContent(x, y)@pre
        if(!getCellContent(x, y).contains(c))
            throw new PreconditionError("Environment", "removeCellContent", "The element is not in the cell");

        // captures
        int width_pre = getWidth();
        int height_pre = getHeight();
        Cell[][] cellNature_pre = new Cell[getWidth()][getHeight()];
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                cellNature_pre[i][j] = getCellNature(i, j);
            }
        }

        Set<InCell>[][] cellContent_pre = (Set<InCell>[][]) new Set[getWidth()][getHeight()];
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                cellContent_pre[i][j] = new HashSet<>(getCellContent(i, j));
            }
        }

        // pre-invariant
        checkInvariant();

        // run
        delegate.removeCellContent(x, y, c);

        // post-invariant
        checkInvariant();

        // const: getWidth()
        if(getWidth() != width_pre)
            throw new PostconditionError("Environment", "removeCellContent", "getWidth() is supposed to be constant");
        // const: getHeight()
        if(getHeight() != height_pre)
            throw new PostconditionError("Environment", "removeCellContent", "getHeight() is supposed to be constant");

        // post: c \notin getCellContent(x, y)
        if(getCellContent(x, y).contains(c))
            throw new PostconditionError("Environment", "removeCellContent", "c notin getCellContent(x, y)");

        // post: \forall InCell c2 \in getCellContent(x, y) c2 \in getCellContent(x, y)@pre
        //       && \forall InCell c2 \in getCellContent(x, y)@pre c2 != c => c2 \in getCellContent(x, y)
        for(InCell c2: getCellContent(x, y)) {
            if(!(cellContent_pre[x][y].contains(c2)))
                throw new PostconditionError("Environment", "removeCellContent", "The content of the cell has changed unexpectedly");
        }
        for(InCell c2: cellContent_pre[x][y]) {
            if(c != c2 && !getCellContent(x, y).contains(c2))
                throw new PostconditionError("Environment", "removeCellContent", "The content of the cell has changed unexpectedly");
        }

        // post: \forall i \in [0..getWidth()[ \forall j \in [0..getHeight()[
        //         (i != x || j != y) => getCellContent(i, j) == getCellContent(i, j)@pre
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                if((i != x || j != y) && !getCellContent(i, j).equals(cellContent_pre[i][j]))
                    throw new PostconditionError("Environment", "removeCellContent", "The content of another cell has changed");
            }
        }

        // post: \forall i \in [0..getWidth()[ \forall j \in [0..getHeight()[
        //         getCellNature(x, y) == getCellNature(x, y)@pre
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                if(getCellNature(i, j) != cellNature_pre[i][j])
                    throw new PostconditionError("Environment", "removeCellContent", "The nature of a cell has changed");
            }
        }
    }

    @Override
    public void dig(int x, int y) {
        // pre: \not \exists Item i \in getCellContent(x, y+1)
        for(InCell ic: getCellContent(x, y+1)) {
            if(ic instanceof Item)
                throw new PreconditionError("Environment", "dig", "There is an Item above this cell");
        }

        super.dig(x, y);
    }
}
