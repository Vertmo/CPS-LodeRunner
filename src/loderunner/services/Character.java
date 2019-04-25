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

    /* Operators */

    // Important: un garde ne peut pas se déplacer vers la case d'un autre garde, en revanche
    // un garde peut se déplacer vers la case d'un joueur (le tuant) et un joueur peut se déplacer
    // vers la case d'un garde (se tuant)

    // post: getHgt() == getHgt()@pre
    // post: (getCol()@pre == 0) => getCol() == getCol()@pre
    // post: getEnvi().getCellNature(getCol()@pre-1,getHgt()) \in { PLT, MTL, TRP }
    //       => getCol() == getCol()@pre
    // post: getEnvi().getCellNature(getCol()@pre, getHgt()) \notin { LAD, HDR }
    //       && getEnvi().getCellNature(getCol()@pre, getHgt()-1) \notin { PLT, MTL, TRP, LAD }
    //       && \not \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()-1)
    //       => getCol() == getCol()@pre
    // post: this \in Guard && \exists Guard g \in getEnvi().getCellContent(getCol()@pre-1, getHgt())
    //       => getCol() == getCol()@pre
    // post: getCol()@pre != 0
    //       && getEnvi().getCellNature(getCol()@pre-1 ,getHgt()) \notin { PLT, MTL, TRP }
    //       && (getEnvi().getCellNature(getCol()@pre, getHgt()) \in { LAD, HDR }
    //           || getEnvi().getCellNature(getCol()@pre, getHgt()-1) \in { PLT, MTL, TRP, LAD }
    //           || \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()-1))
    //       && (this \notin Guard || \not \exists Guard g \in getEnvi().getCellContent(getCol()@pre-1, getHgt()))
    //       => getCol() == getCol()@pre - 1
    public void goLeft();

    // post: getHgt() == getHgt()@pre
    // post: (getCol()@pre == getEnvi().getWidth()-1) => getCol() == getCol()@pre
    // post: getEnvi().getCellNature(getCol()@pre+1, getHgt()) \in { PLT, MTL, TRP }
    //       => getCol() == getCol()@pre
    // post: getEnvi().getCellNature(getCol()@pre, getHgt()) \notin { LAD, HDR }
    //       && getEnvi().getCellNature(getCol()@pre, getHgt()-1) \notin { PLT, MTL, TRP, LAD }
    //       && \not \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()-1)
    //       => getCol() == getCol()@pre
    // post: this \in Guard && \exists Guard g \in getEnvi().getCellContent(getCol()@pre+1, getHgt())
    //       => getCol() == getCol()@pre
    // post: getCol()@pre != 0
    //       && getEnvi().getCellNature(getCol()@pre+1,getHgt()) \notin { PLT, MTL, TRP }
    //       && (getEnvi().getCellNature(getCol()@pre, getHgt()) \in { LAD, HDR }
    //           || getEnvi().getCellNature(getCol()@pre, getHgt()-1) \in { PLT, MTL, TRP, LAD }
    //           || \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()-1))
    //       && (this \notin Guard || \not \exists Guard g \in getEnvi().getCellContent(getCol()@pre+1, getHgt()))
    //       => getCol() == getCol()@pre + 1
    public void goRight();

    // post: getCol() == getCol()@pre
    // post: (getHgt()@pre == getEnvi().getHeight()-1) => getHgt() == getHgt()@pre
    // post: getEnvi().getCellNature(getCol(), getHgt()@pre) == LAD
    //       && getEnvi().getCellNature(getCol(), getHgt()@pre+1) \notin { PLT, MTL, TRP }
    //       && (this \notin Guard || \not \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()@pre+1))
    //       => getHgt() == getHgt()@pre + 1
    // post: getEnvi().getCellNature(getCol(), getHgt()@pre) != LAD
    //       || getEnvi().getCellNature(getCol(), getHgt()@pre+1) \in { PLT, MTL, TRP }
    //       || (this \in Guard && \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()@pre+1))
    //       => getHgt() == getHgt()@pre
    public void goUp();

    // Important: un garde ne marche pas sur la tete du joueur mais tombe sur sa case (le tuant)
    // cependant un garde marche sur la tete d'un autre garde

    // post: (getCol() == getCol()@pre
    // post: (getHgt()@pre == 0) => getHgt() == getHgt()@pre
    // post: getEnvi().getCellNature(getCol(), getHgt()@pre-1) \notin { PLT, MTL, TRP }
    //       && \not \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()@pre-1)
    //       => getHgt() == getHgt()@pre - 1
    // post: getEnvi().getCellNature(getCol(), getHgt()@pre-1) \in { PLT, MTL, TRP }
    //       || \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()@pre-1))
    //       => getHgt() == getHgt()@pre
    public void goDown();
}
