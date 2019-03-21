package loderunner.services;

import java.util.Set;

public interface Environment extends Screen {
    /* Observators */

    // pre: 0 <= x < getWidth() && 0 <= y < getHeight()
    public Set<InCell> getCellContent(int x, int y);

    /* Invariants */

    // inv: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[
    //        \forall Character c1 \in getCellContent(x, y)
    //          \forall Character c2 \in getCellContent(x, y)
    //            c1 == c2
    // inv: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[
    //        getCellNature(x, y) \in { PLT, MTL } => getCellContent(x, y).isEmpty()
    // inv: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[
    //        \exists Treasure t \in getCellContent(x, y) =>
    //        (getCellNature(x, y) == EMP && getCellNature(x, y-1) \in { PLT, MTL })
}
