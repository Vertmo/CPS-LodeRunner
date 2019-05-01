package loderunner.services;

public interface Screen {
    /* Observators */

    // const
    public int getWidth();

    // const
    public int getHeight();

    // pre: 0 <= x < getWidth() && 0 <= y < getHeight()
    public Cell getCellNature(int x, int y);

    /* Constructors */

    // pre: 0 < w && 0 < h
    // post: getWidth() == w
    // post: getHeight() == h
    // post: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[ getCellNature(x, y) == EMP
    public void init(int w, int h);

    /* Operators */

    // pre: getCellNature(x, y) == PLT
    // post: getCellNature(x, y) == HOL
    // post: \forall i \in [0..getWidth()[ \forall j \in [0..getHeight()[
    //         (i != x || j != y) => getCellNature(i, j) == getCellNature(i, j)@pre
    public void dig(int x, int y);

    // pre: getCellNature(x, y) == HOL
    // post: getCellNature(x, y) == PLT
    // post: \forall i \in [0..getWidth()[ \forall j \in [0..getHeight()[
    //         (i != x || j != y) => getCellNature(i, j) == getCellNature(i, j)@pre
    public void fill(int x, int y);

    // pre: getCellNature(x, y) = TRP
    // post: getCellNature(x, y) = EMP
    // post: \forall i \in [0..getWidth()[ \forall j \in [0..getHeight()[
    //         (i != x || j != y) => getCellNature(i, j) == getCellNature(i, j)@pre
    public void triggerTrap(int x, int y);

    // pre: getCellNature(x, y) = DOR
    // post: getCellNature(x, y) = EMP
    // post: \forall i \in [0..getWidth()[ \forall j \in [0..getHeight()[
    //         (i != x || j != y) => getCellNature(i, j) == getCellNature(i, j)@pre
    public void openDoor(int x, int y);
}
