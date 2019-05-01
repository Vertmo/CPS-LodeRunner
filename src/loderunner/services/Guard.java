package loderunner.services;

public interface Guard extends /* refine */ Character, Cloneable {
    /* Observators */

    // const
    public int getId();

    // const
    public int getInitCol();
    // const
    public int getInitHgt();

    public Move getBehaviour();

    // const.
    public Character getTarget();

    // pre: getEnvi().getCellNature(getCol(), getHgt()) == HOL
    public int getTimeInHole();

    /* Invariants */

    // inv: getEnvi().getCellNature(getCol(), getHgt()) == LAD
    //      && getHgt() < getTarget().getHgt()
    //      && (getCol() != getTarget().getCol() && (getEnvi().getCellNature(getCol(), getHgt()-1) \in { PLT, MTL }
    //          || \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()-1))
    //          => getTarget().getHgt() - getHgt() < |getTarget().getCol() - getCol()|)
    //      => getBehaviour() == Up

    // inv: (getEnvi().getCellNature(getCol(), getHgt()) \in { LAD, HDR }
    //        (cell_below == Cell.LAD && \not \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()-1))
    //      && getHgt() > getTarget().getHgt()
    //      && (getCol() != getTarget().getCol() && (getEnvi().getCellNature(getCol(), getHgt()-1) \in { PLT, MTL }
    //          || \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()-1))
    //          => getHgt() - getTarget().getHgt() < |getTarget().getCol() - getCol()|)
    //      => getBehaviour() == Down

    // inv: getEnvi().getCellNature(getCol(), getHgt()) == LAD
    //      && getHgt() == getTarget().getHgt()
    //      && (getCol() == getTarget().getCol() || |getCol()-getTarget().getCol()| > |getHgt()-getTarget().getHgt()|)
    //      => getBehaviour() == Neutral

    // inv: (getEnvi().getCellNature(getCol(), getHgt()-1) \in { PLT, MTL }
    //       || (getEnvi().getCellNature(getCol(), getHgt()-1) == LAD
    //           && (getEnvi().getCellNature(getCol(), getHgt()) != LAD
    //               || getEnvi().getCellNature(getCol()-1, getHgt()-1) \in { PLT, MTL }
    //               || getEnvi().getCellNature(getCol()-1, getHgt()) = HDR))
    //       || \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()-1))
    //      && getCol() < getTarget().getCol()
    //      && (getHgt() != getTarget().getHgt() && (getEnvi().getCellNature(getCol(), getHgt()) == LAD)
    //          => getTarget().getCol() - getCol() < |getTarget().getHgt() - getHgt()|)
    //      => getBehaviour() == Left

    // inv: (getEnvi().getCellNature(getCol(), getHgt()-1) \in { PLT, MTL }
    //       || (getEnvi().getCellNature(getCol(), getHgt()-1) == LAD
    //           && (getEnvi().getCellNature(getCol(), getHgt()) != LAD
    //               || getEnvi().getCellNature(getCol()+1, getHgt()-1) \in { PLT, MTL }
    //               || getEnvi().getCellNature(getCol()+1, getHgt()) = HDR))
    //       || \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()-1)))
    //      && getCol() > getTarget().getCol()
    //      && (getHgt() != getTarget().getHgt() && (getEnvi().getCellNature(getCol(), getHgt()) == LAD)
    //          => getCol() - getTarget().getCol() < |getTarget().getHgt() - getHgt()|)
    //      => getBehaviour() == Right

    // inv: (getEnvi().getCellNature(getCol(), getHgt()-1) \in { PLT, MTL }
    //       || (getEnvi().getCellNature(getCol(), getHgt()-1) == LAD
    //           && getEnvi().getCellNature(getCol(), getHgt()) != LAD)
    //       || \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()-1))
    //      && getCol() == getTarget().getCol()
    //      && (getHgt() == getTarget().getHgt() ||
    //                      (getEnvi().getCellNature(getCol(), getHgt()) != LAD
    //                       && (getEnvi().getCellNature(getCol(), getHgt()-1) != Cell.LAD)
    //      => getBehaviour() == Neutral

    /* Constructors */

    // pre: e.getCellNature(x, y) == EMP
    // post: getEnvi() == e
    // post: getCol() == getInitCol() == x
    // post: getHgt() == getInitCol() == y
    // post: getTarget() == t
    public void init(Environment e, Character t, int x, int y);

    /* Operators */

    // pre: getEnvi().getCellNature(getCol(), getHgt()) == HOL
    // post: getCol()@pre == 0
    //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
    // post: getEnvi().getCellNature(getCol()@pre-1, getHgt()@pre+1) \in { MTL, PLT }
    //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
    // post: \exists Guard g \in getEnvi().getCellContent(getCol()@pre-1, getHgt()@pre+1)
    //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
    // post: getCol()@pre != 0
    //       && getEnvi().getCellNature(getCol()@pre-1, getHgt()@pre+1) \notin { MTL, PLT }
    //       && \not \exists Guard g \in getEnvi().getCellContent(getCol()@pre-1, getHgt()@pre+1)
    //       => (getCol() == getCol()@pre-1 && getHgt() == getHgt()@pre+1)
    public void climbLeft();

    // pre: getEnvi().getCellNature(getCol(), getHgt()) == HOL
    // post: getCol()@pre == getEnvi().getWidth()-1
    //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
    // post: getEnvi().getCellNature(getCol()@pre+1, getHgt()@pre+1) \in { MTL, PLT }
    //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
    // post: \exists Guard g \in getEnvi().getCellContent(getCol()@pre+1, getHgt()@pre+1)
    //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
    // post: getCol()@pre != getEnvi().getWidth()-1
    //       && getEnvi().getCellNature(getCol()@pre+1, getHgt()@pre+1) \notin { MTL, PLT }
    //       && \not \exists Guard g \in getEnvi().getCellContent(getCol()@pre+1, getHgt()@pre+1)
    //       => (getCol() == getCol()@pre+1 && getHgt() == getHgt()@pre+1)
    public void climbRight();

    // def: willFall() = getEnvi().getCellNature(getCol()@pre, getHgt()@pre-1) \in { HOL, EMP, HDR }
    //                   && \not \exists Guard g \in getEnvi().getCellContent(getCol()@pre, getHgt()@pre-1)
    //                   && getEnvi().getCellNature(getCol()@pre, getHgt()@pre) \notin { LAD, HDR }
    // post: willFall() => step() == goDown()
    // post: \not willFall()
    //       && getEnvi().getCellNature(getCol()@pre, getHgt()@pre) == HOL && getTimeInHole()@pre < 5
    //       => getTimeInHole() == getTimeInHole()@pre + 1
    // post: \not willFall()
    //       && getEnvi().getCellNature(getCol()@pre, getHgt()@pre) == HOL && getTimeInHole()@pre == 5
    //       => (getBehaviour()@pre == Left => step() == climbLeft()
    //           && getBehaviour()@pre == Right => step() == climbRight())
    // post: \not willFall() && getEnvi().getCellNature(getCol()@pre, getHgt()@pre) != HOL
    //       => (getBehaviour()@pre == Left => step() == goLeft()
    //           && getBehaviour()@pre == Right => step() == goRight()
    //           && getBehaviour()@pre == Up => step() == goUp()
    //           && getBehaviour()@pre == Down => step() == goDown())
    public void step();

    public Guard clone();
}
