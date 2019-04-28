package loderunner.services;

public interface Player extends /* refine */ Character {
    /* Observators */

    // const
    public Engine getEngine();

    public int getNbKeys();

    /* Constructors */

    // pre: e.getCellNature(x, y) == EMP
    // post: getEnvi() == e
    // post: getCol() == x
    // post: getHgt() == y
    // post: getEngine() == eg
    // post: getNbKeys() == 0
    public void init(Environment e, Engine eg, int x, int y);

    // pre: getEnvi().getCellNature(x, y) == EMP
    // post: getCol() == x
    // post: getHgt() == y
    // post: getNbKeys() == getNbKeys()@pre
    public void teleport(int x, int y);

    // post: getNbKeys() = getNbKeys()@pre + 1
    public void grabKey();

    /* Operators */

    // def: willFall() = getEnvi().getCellNature(getCol()@pre, getHgt()@pre-1) \in { HOL, EMP, HDR }
    //                   && \not \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()@pre-1)
    //                   && getEnvi().getCellNature(getCol()@pre, getHgt()@pre) \notin { LAD, HDR }
    // post: willFall() => step() == goDown()
    // post: \not willFall() =>
    //       (getEngine().getNextCommand() == Left => step() == goLeft()
    //        && getEngine().getNextCommand() == Right => step() == goRight()
    //        && getEngine().getNextCommand() == Up => step() == goUp()
    //        && getEngine().getNextCommand() == Down => step() == goDown())
    // post: \not willFall()
    //       && getEngine().getNextCommand() == DigL
    //       && (getEnvi().getCellNature(getCol()@pre, getHgt()@pre-1) \in { PLT, MTL, LAD }
    //           || \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()@pre-1))
    //       && getEnvi().getCellNature(getCol()@pre-1, getHgt()@pre) \in { EMP, HOL }
    //       && getEnvi().getCellContent(getCol()@pre-1,getHgt()@pre).isEmpty()
    //       && getEnvi().getCellNature(getCol()@pre-1, getHgt()@pre-1)@pre == PLT
    //       => getEnvi().getCellNature(getCol()@pre-1, getHgt()@pre-1) == HOL
    // post: \not willFall()
    //       && getEngine().getNextCommand() == DigR
    //       && (getEnvi().getCellNature(getCol()@pre, getHgt()@pre-1) \in { PLT, MTL, LAD }
    //           || \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()@pre-1))
    //       && getEnvi().getCellNature(getCol()@pre+1, getHgt()@pre) \in { EMP, HOL }
    //       && getEnvi().getCellContent(getCol()@pre+1,getHgt()@pre).isEmpty()
    //       && getEnvi().getCellNature(getCol()@pre+1, getHgt()@pre-1)@pre == PLT
    //       => getEnvi().getCellNature(getCol()@pre+1, getHgt()@pre-1) == HOL
    // post: getEngine().getNextCommand() \in { DigL, DigR, Neutral }
    //       => getCol() == getCol()@pre && getHgt() == getHgt()@pre
    public void step();

    public Player clone();
}
