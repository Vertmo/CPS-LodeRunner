package loderunner.contracts;

import loderunner.contracts.errors.PostconditionError;
import loderunner.contracts.errors.PreconditionError;
import loderunner.services.Cell;
import loderunner.services.Character;
import loderunner.services.Environment;
import loderunner.services.Guard;
import loderunner.services.Move;

public class GuardContract extends CharacterContract implements Guard {
    private final Guard delegate;

    public GuardContract(Guard delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    @Override
    public int getInitCol() {
        return delegate.getInitCol();
    }

    @Override
    public int getInitHgt() {
        return delegate.getInitHgt();
    }

    @Override
    public Move getBehaviour() {
        return delegate.getBehaviour();
    }

    @Override
    public Character getTarget() {
        return delegate.getTarget();
    }

    @Override
    public int getTimeInHole() {
        // pre: getEnvi().getCellNature(getCol(), getHgt()) == HOL
        if(getEnvi().getCellNature(getCol(), getHgt()) != Cell.HOL)
            throw new PreconditionError("Guard", "getTimeInHole", "getEnvi().getCellNature(getCol(), getHgt()) == HOL");

        return delegate.getTimeInHole();
    }

    @Override
    public void checkInvariant() {
        super.checkInvariant();

        // inv: getEnvi().getCellNature(getCol(), getHgt()) == LAD
        //      && getHgt() < getTarget().getHgt()
        //      && (getEnvi().getCellNature(getCol(), getHgt()-1) \in { PLT, MTL }
        //          || \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()-1)
        //          => getTarget().getHgt() - getHgt() < |getTarget().getCol() - getCol()|)
        //      => getBehaviour() == Up
        // TODO
        // inv: getEnvi().getCellNature(getCol(), getHgt()) == LAD
        //      && getHgt() > getTarget().getHgt()
        //      && (getEnvi().getCellNature(getCol(), getHgt()-1) \in { PLT, MTL }
        //          || \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()-1)
        //          => getHgt() - getTarget().getHgt() < |getTarget().getCol() - getCol()|)
        //      => getBehaviour() == Down
        // TODO
        // inv: getEnvi().getCellNature(getCol(), getHgt()) == LAD
        //      && getHgt() == getTarget().getHgt()
        //      && !(getEnvi().getCellNature(getCol(), getHgt()-1) \in { PLT, MTL }
        //          || \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()-1))
        //      => getBehaviour() == Neutral
        // TODO
        // inv: (getEnvi().getCellNature(getCol(), getHgt()) \in { HDR, HOL }
        //       || getEnvi().getCellNature(getCol(), getHgt()-1) \in { PLT, MTL }
        //       || \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()-1))
        //      && getCol() < getTarget().getCol()
        //      && (getEnvi().getCellNature(getCol(), getHgt()) == LAD
        //          => getTarget().getCol() - getCol() < |getTarget().getHgt() - getHgt()|)
        //      => getBehaviour() == Left
        // TODO
        // inv: (getEnvi().getCellNature(getCol(), getHgt()) \in { HDR, HOL }
        //       || getEnvi().getCellNature(getCol(), getHgt()-1) \in { PLT, MTL }
        //       || \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()-1))
        //      && getCol() > getTarget().getCol()
        //      && (getEnvi().getCellNature(getCol(), getHgt()) == LAD
        //          => getCol() - getTarget().getCol() < |getTarget().getHgt() - getHgt()|)
        //      => getBehaviour() == Right
        // TODO
        // inv: (getEnvi().getCellNature(getCol(), getHgt()) \in { HDR, HOL }
        //       || getEnvi().getCellNature(getCol(), getHgt()-1) \in { PLT, MTL }
        //       || \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()-1))
        //      && getCol() == getTarget().getCol()
        //      && getEnvi().getCellNature(getCol(), getHgt()) != LAD
        //      => getBehaviour() == Neutral
        // TODO
    }

    @Override
    public void init(Environment e, Character t, int x, int y) {
        // pre: e.getCellNature(x, y) == EMP
        if(e.getCellNature(x, y) != Cell.EMP)
            throw new PreconditionError("Guard", "init", "e.getCellNature(x, y) == EMP");

        // run
        delegate.init(e, t, x, y);

        // post-invariant
        checkInvariant();

        // post: getEnvi() == e
        if(getEnvi() != e)
            throw new PostconditionError("Guard", "init", "getEnvi() == e");
        // post: getCol() == getInitCol() == x
        if(getCol() != x || getInitCol() != x)
            throw new PostconditionError("Guard", "init", "getCol() == getInitCol() == x");
        // post: getHgt() == getInitCol() == y
        if(getHgt() != y || getInitHgt() != y)
            throw new PostconditionError("Guard", "init", "getHgt() == getInitCol() == y");
        // post: getTarget() == t
        if(getTarget() != t)
            throw new PostconditionError("Guard", "init", "getTarget() == t");
    }

    @Override
    public void climbLeft() {
        // pre: getEnvi().getCellNature(getCol(), getHgt()) == HOL
        if(getEnvi().getCellNature(getCol(), getHgt()) != Cell.HOL)
            throw new PreconditionError("Guard", "climbLeft", "getEnvi().getCellNature(getCol(), getHgt()) == HOL");

        // pre-invariant
        checkInvariant();

        // run
        delegate.climbLeft();

        // post-invariant
        checkInvariant();

        // post: getCol()@pre == 0
        //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
        // TODO
        // post: getEnvi().getCellNature(getCol()@pre-1, getHgt()@pre+1) \in { MTL, PLT }
        //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
        // TODO
        // post: \exists Guard g \in getEnvi().getCellContent(getCol()@pre-1, getHgt()@pre+1)
        //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
        // TODO
        // post: getCol() != 0
        //       && getEnvi().getCellNature(getCol()@pre-1, getHgt()@pre+1) \notin { MTL, PLT }
        //       && \not \exists Guard g \in getEnvi().getCellContent(getCol()@pre-1, getHgt()@pre+1)
        //       => (getCol() == getCol()@pre-1 && getHgt() == getHgt()@pre+1)
        // TODO
    }

    @Override
    public void climbRight() {
        // pre: getEnvi().getCellNature(getCol(), getHgt()) == HOL
        if(getEnvi().getCellNature(getCol(), getHgt()) != Cell.HOL)
            throw new PreconditionError("Guard", "climbRight", "getEnvi().getCellNature(getCol(), getHgt()) == HOL");

        // pre-invariant
        checkInvariant();

        // run
        delegate.climbRight();

        // post-invariant
        checkInvariant();

        // post: getCol()@pre == getEnvi().getWidth()-1
        //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
        // TODO
        // post: getEnvi().getCellNature(getCol()@pre+1, getHgt()@pre+1) \in { MTL, PLT }
        //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
        // TODO
        // post: \exists Guard g \in getEnvi().getCellContent(getCol()@pre+1, getHgt()@pre+1)
        //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
        // TODO
        // post: getCol() != getEnvi().getWidth()-1
        //       && getEnvi().getCellNature(getCol()@pre+1, getHgt()@pre+1) \notin { MTL, PLT }
        //       && \not \exists Guard g \in getEnvi().getCellContent(getCol()@pre+1, getHgt()@pre+1)
        //       => (getCol() == getCol()@pre+1 && getHgt() == getHgt()@pre+1)
        // TODO
    }

    @Override
    public void step() {
        // pre-invariant
        checkInvariant();

        // run
        delegate.step();

        // post-invariant
        checkInvariant();

        // post: willFall() => step() == goDown()
        // TODO
        // post: \not willFall()
        //       && getEnvi().getCellNature(getCol()@pre, getHgt()@pre) == HOL && getTimeInHole()@pre < 5
        //       => getTimeInHole() == getTimeInHole()@pre + 1
        // post: getBehaviour() == Neutral
        //       => getCol() == getCol()@pre && getHgt() == getHgt()@pre
        // TODO
        // post: \not willFall()
        //       && getEnvi().getCellNature(getCol()@pre, getHgt()@pre) == HOL && getTimeInHole()@pre == 5
        //       => (getBehaviour()@pre == Left => step() == climbLeft()
        //           && getBehaviour()@pre == Right => step() == climbRight())
        // TODO
        // post: \not willFall() && getEnvi().getCellNature(getCol()@pre, getHgt()@pre) != HOL
        //       => (getBehaviour()@pre == Left => step() == goLeft()
        //           && getBehaviour()@pre == Right => step() == goRight()
        //           && getBehaviour()@pre == Up => step() == goUp()
        //           && getBehaviour()@pre == Down => step() == goDown())
        // TODO
    }
}
