package loderunner.services;

public interface Screen {
    /* Observators */

    // const
    public int getHeight();

    // const
    public int getWidth();

    // pre: 0 <= x < getWidth() && 0 <= y < getHeight()
    public Cell getCellNature(int x, int y);

    /* Constructors */

    // pre: 0 < w && 0 < h
    // post: getWidth() = w
    // post: getHeight() = h
    // post: \forall x \in [0..getWidth()[ \forall y \in [0..getHeight()[ getCellNature(x, y) == EMP
    public void init(int w, int h);

    /* Operators */

    // pre: getCellNature(x, y) == PLT
    // post: getCellNature(x, y) == HOL
    // post: \forall i \in [0..width()[ \forall j \in [0..height()[
    //         (i != x || j != y) => getCellNature(i, j) == getCellNature(i, j)@pre
    public void dig(int x, int y);

    // pre: getCellNature(x, y) == HOL
    // post: getCellNature(x, y) == PLT
    // post: \forall i \in [0..width()[ \forall j \in [0..height()[
    //         (i != x || j != y) => getCellNature(i, j) == getCellNature(i, j)@pre
    public void fill(int x, int y);
}
