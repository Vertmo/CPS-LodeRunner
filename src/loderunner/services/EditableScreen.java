package loderunner.services;

public interface EditableScreen extends Screen {
    /* Observators */
    public boolean isPlayable();

    /* Invariants */

    // inv: isPlayable() ==
    //      \forall x \in [0..width()[ \forall y \in [0..height()[ getCellNature(x, y) != HOL
    //      && \forall x \in [0..width()[ getCellNature(x, 0) == MTL

    /* Operators */

    // pre: 0 <= x < getWidth() && 0 <= y < getHeight()
    // post: getCellNature(x, y) = c
    // post: \forall i \in [0..width()[ \forall j \in [0..height()[ getCellNature(x, y)
    //         (i != x || j != y) => getCellNature(i, j) == getCellNature(i, j)@pre
    public void setNature(int x, int y, Cell c);
}
