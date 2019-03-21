package loderunner.services;

public interface Character extends InCell {
    /* Observators */

    // const
    public Environment getEnvi();

    public int getCol();
    public int getHgt();

    /* Constructors */

    // pre: e.getCellNature(x, y) == EMP
    // post: getEnvi() == e
    // post: getCol() == x
    // post: getHgt() == y
    public void init(Environment e, int x, int y);

    /* Invariants */
    // inv: getEnvi().getCellNature(getCol(), getHgt()) \in { EMP, HOL, LAD, HDR }
    // inv: getEnvi().getCellContent(getCol(), getHgt()).contains(this)

    /* Operators */

    // post: getHgt() == getHgt()@pre
    // post: (getCol()@pre == 0) => getCol() == getCol()@pre
    // post: getEnvi().getCellNature(getCol()@pre-1,getHgt()) \in { PLT, MTL, LAD }
    //       => getCol() == getCol()@pre
    // post: getEnvi().getCellNature(getCol()@pre, getHgt()) \notin { LAD, HDR }
    //       && getEnvi().getCellNature(getCol()@pre, getHgt()-1) \notin { PLT, MTL }
    //       && \not \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()-1)
    //       => getCol() == getCol()@pre
    // post: \exists Character c \in getEnvi().getCellContent(getCol()@pre-1, getHgt())
    //       => getCol() == getCol()@pre
    // post: getCol()@pre != 0
    //       && getEnvi().getCellNature(getCol()@pre-1 ,getHgt()) \notin { PLT, MTL, LAD }
    //       && (getEnvi().getCellNature(getCol()@pre, getHgt()) \in { LAD, HDR }
    //           || getEnvi().getCellNature(getCol()@pre, getHgt()-1) \in { PLT, MTL }
    //           || \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()-1))
    //       && \not \exists Character c \in getEnvi().getCellContent(getCol()@pre-1, getHgt())
    //       => getCol() == getCol()@pre - 1
    public void goLeft();

    // post: getHgt() == getHgt()@pre
    // post: (getCol()@pre == getEnvi().getWidth()-1) => getCol() == getCol()@pre
    // post: getEnvi().getCellNature(getCol()@pre+1, getHgt()) \in { PLT, MTL, LAD }
    //       => getCol() == getCol()@pre
    // post: getEnvi().getCellNature(getCol()@pre, getHgt()) \notin { LAD, HDR }
    //       && getEnvi().getCellNature(getCol()@pre, getHgt()-1) \notin { PLT, MTL }
    //       && \not \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()-1)
    //       => getCol() == getCol()@pre
    // post: \exists Character c \in getEnvi().getCellContent(getCol()@pre+1, getHgt())
    //       => getCol() == getCol()@pre
    // post: getCol()@pre != 0
    //       && getEnvi().getCellNature(getCol()@pre+1,getHgt()) \notin { PLT, MTL, LAD }
    //       && (getEnvi().getCellNature(getCol()@pre, getHgt()) \in { LAD, HDR }
    //           || getEnvi().getCellNature(getCol()@pre, getHgt()-1) \in { PLT, MTL }
    //           || \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()-1))
    //       && \not \exists Character c \in getEnvi().getCellContent(getCol()@pre+1, getHgt())
    //       => getCol() == getCol()@pre + 1
    public void goRight();

    // post: getCol() == getCol()@pre
    // post: getEnvi().getCellNature(getCol(), getHgt()@pre) == LAD
    //       && getEnvi().getCellNature(getCol(), getHgt()@pre+1) \in { EMP, HOL, LAD, HDR }
    //       && \not \exists Character c \in getEnvi().getCellContent(getCol(), getHgt()@pre+1)
    //       => getHgt() == getHgt()@pre + 1
    // post: getEnvi().getCellNature(getCol(), getHgt()@pre) != LAD
    //       || getEnvi().getCellNature(getCol(), getHgt()@pre+1) \notin { EMP, HOL, LAD, HDR }
    //       || \exists Character c \in getEnvi().getCellContent(getCol(), getHgt()@pre+1)
    //       => getHgt() == getHgt()@pre
    public void goUp();

    // post: getCol() == getCol()@pre
    // post: getEnvi().getCellNature(getCol(), getHgt()@pre-1) \in { EMP, HOL, LAD, HDR }
    //       && \not \exists Character c \in getEnvi().getCellContent(getCol(), getHgt()@pre-1)
    //       => getHgt() == getHgt()@pre - 1
    // post: getEnvi().getCellNature(getCol(), getHgt()@pre-1) \notin { EMP, HOL, LAD, HDR }
    //       || \exists Character c \in getEnvi().getCellContent(getCol(), getHgt()@pre-1)
    //       => getHgt() == getHgt()@pre
    public void goDown();
}
