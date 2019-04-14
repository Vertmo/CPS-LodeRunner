package loderunner.services;

import java.util.Set;

public interface Environment extends /* include */ Screen {
    /* Observators */

    // pre: 0 <= x < getWidth() && 0 <= y < getHeight()
    public Set<InCell> getCellContent(int x, int y);

    /* Invariants */

    // inv: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[
    //        \forall Guard g1 \in getCellContent(x, y)
    //          \forall Guard g2 \in getCellContent(x, y)
    //            g1 == g2
    // inv: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[
    //        getCellNature(x, y) \in { PLT, MTL } => getCellContent(x, y).isEmpty()
    // inv: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[
    //        \exists Treasure t \in getCellContent(x, y) =>
    //        (getCellNature(x, y) == EMP && getCellNature(x, y-1) \in { PLT, MTL })

    /* Constructors */

    // pre: 0 < w && 0 < h
    // post: getWidth() == w
    // post: getHeight() == h
    // post: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[ getCellNature(x, y) == EMP
    // post: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[
    //         getCellContent(x, y).isEmpty()
    @Override
    public void init(int w, int h);

    // post: getWidth() == s.getWidth()
    // post: getHeight() == s.getHeight()
    // post: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[
    //         getCellNature(x, y) == s.getCellNature(x, y)
    // post: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[
    //         getCellContent(x, y).isEmpty()
    public void init(Screen s);

    /* Operators */

    // pre: getCellNature(x, y) == EMP && getCellNature(x, y-1) \in { PLT, MTL }
    // post: c \in getCellContent(x, y)
    // post: \forall InCell c2 \in getCellContent(x, y)@pre c2 \in getCellContent(x, y)
    //       && \forall InCell c2 \in getCellContent(x, y) c2 != c => c2 \in getCellContent(x, y)@pre
    // post: \forall i \in [0..getWidth()[ \forall j \in [0..getHeight()[
    //         (i != x || j != y) => getCellContent(i, j) == getCellContent(i, j)@pre
    // post: \forall i \in [0..getWidth()[ \forall j \in [0..getHeight()[
    //         getCellNature(x, y) == getCellNature(x, y)@pre
    public void addCellContent(int x, int y, InCell c);

    // pre: c \in getCellContent(x, y)@pre
    // post: c \notin getCellContent(x, y)
    // post: \forall InCell c2 \in getCellContent(x, y) c2 \in getCellContent(x, y)@pre
    //       && \forall InCell c2 \in getCellContent(x, y)@pre c2 != c => c2 \in getCellContent(x, y)
    // post: \forall i \in [0..getWidth()[ \forall j \in [0..getHeight()[
    //         (i != x || j != y) => getCellContent(i, j) == getCellContent(i, j)@pre
    // post: \forall i \in [0..getWidth()[ \forall j \in [0..getHeight()[
    //         getCellNature(x, y) == getCellNature(x, y)@pre
    public void removeCellContent(int x, int y, InCell c);
}
