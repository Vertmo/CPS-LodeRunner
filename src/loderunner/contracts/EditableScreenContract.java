package loderunner.contracts;

import loderunner.contracts.errors.InvariantError;
import loderunner.contracts.errors.PostconditionError;
import loderunner.contracts.errors.PreconditionError;
import loderunner.services.Cell;
import loderunner.services.EditableScreen;

public class EditableScreenContract extends ScreenContract implements EditableScreen {
    private final EditableScreen delegate;

    public EditableScreenContract(EditableScreen delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public void checkInvariant() {
        super.checkInvariant();

        // inv: isPlayable() ==
        //      \forall x \in [0..width()[ \forall y \in [0..height()[ getCellNature(x, y) != HOL
        //      && \forall x \in [0..width()[ getCellNature(x, 0) == MTL
        boolean is_playable = true;
        for(int x = 0; x < getWidth(); x++) {
            for(int y = 0; y < getHeight(); y++) {
                if(getCellNature(x, y) == Cell.HOL) is_playable = false;
            }
        }
        for(int x = 0; x < getWidth(); x++) {
            if(getCellNature(x, 0) != Cell.MTL) is_playable = false;
        }
        if(isPlayable() != is_playable)
            throw new InvariantError("EditableScreen", "isPlayable()");
    }

    @Override
    public boolean isPlayable() {
        return delegate.isPlayable();
    }

    @Override
    public void setNature(int x, int y, Cell c) {
        // pre: 0 <= x < getWidth() && 0 <= y < getHeight()
        if(x < 0 || x >= getWidth() || y < 0 || y >= getHeight())
            throw new PreconditionError("EditableScreen", "setNature", "0 <= x < getWidth() && 0 <= y < getHeight()");

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
        delegate.setNature(x, y, c);

        // post-invariant
        checkInvariant();

        // post: getCellNature(x, y) = c
        if(getCellNature(x, y) != c) throw new PostconditionError("EditableScreen", "setNature", "getCellNature(x, y) = c");
        // post: \forall i \in [0..width()[ \forall j \in [0..height()[ getCellNature(x, y)
        //         (i != x || j != y) => getCellNature(i, j) == getCellNature(i, j)@pre
        for(int i = 0; i < getWidth(); i++) {
            for(int j = 0; j < getHeight(); j++) {
                if((i != x || j != y) && getCellNature(i, j) != cellNature_pre[i][j])
                    throw new PostconditionError("EditableScreen", "setNature", "One of the other cells has changed");
            }
        }

    }
}
