package loderunner.contracts;

import loderunner.contracts.errors.InvariantError;
import loderunner.contracts.errors.PostconditionError;
import loderunner.contracts.errors.PreconditionError;
import loderunner.services.Cell;
import loderunner.services.Character;
import loderunner.services.Environment;
import loderunner.services.Guard;
import loderunner.services.InCell;
import loderunner.services.Move;

public class GuardContract extends CharacterContract implements Guard {
    private final Guard delegate;

    public GuardContract(Guard delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public Guard clone() {
        return new GuardContract(delegate.clone());
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
    public boolean isShot() {
        return delegate.isShot();
    }

    @Override
    public void setIsShot(boolean b) {
        delegate.setIsShot(b);
    }

    @Override
    public void checkInvariant() {
        super.checkInvariant();

        int hDist = Math.abs(getTarget().getCol() - getCol());
        int vDist = Math.abs(getTarget().getHgt() - getHgt());
        Cell cell = getEnvi().getCellNature(getCol(), getHgt());
        Cell cell_below = getEnvi().getCellNature(getCol(), getHgt()-1);
        Cell cell_left;
        if(getCol() == 0) cell_left = Cell.MTL;
        else cell_left = getEnvi().getCellNature(getCol()-1, getHgt());
        Cell cell_belowleft;
        if(getCol() == 0) cell_belowleft = Cell.MTL;
        else cell_belowleft = getEnvi().getCellNature(getCol()-1, getHgt()-1);
        Cell cell_right;
        if(getCol() == getEnvi().getWidth()-1) cell_right = Cell.MTL;
        else cell_right = getEnvi().getCellNature(getCol()+1, getHgt());
        Cell cell_belowright;
        if(getCol() == getEnvi().getWidth()-1) cell_belowright = Cell.MTL;
        else cell_belowright = getEnvi().getCellNature(getCol()+1, getHgt()-1);
        boolean guard_below = false;
        for(InCell ic: getEnvi().getCellContent(getCol(), getHgt()-1)) {
            if(ic instanceof Guard) guard_below = true;
        }
        // inv: getEnvi().getCellNature(getCol(), getHgt()) == LAD
        //      && getHgt() < getTarget().getHgt()
        //      && (getCol() != getTarget().getCol() && (getEnvi().getCellNature(getCol(), getHgt()-1) \in { PLT, MTL }
        //          || \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()-1))
        //          => getTarget().getHgt() - getHgt() < |getTarget().getCol() - getCol()|)
        //      => getBehaviour() == Up
        if(cell == Cell.LAD &&
           getHgt() < getTarget().getHgt() &&
           (hDist == 0 || !(cell_below == Cell.PLT || cell_below == Cell.MTL || guard_below) || hDist > vDist) &&
           getBehaviour() != Move.Up)
            throw new InvariantError("Guard", "getBehaviour() should be Up");
        // inv: (getEnvi().getCellNature(getCol(), getHgt()) \in { LAD, HDR }
        //        (cell_below == Cell.LAD && \not \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()-1))
        //      && getHgt() > getTarget().getHgt()
        //      && (getCol() != getTarget().getCol() && (getEnvi().getCellNature(getCol(), getHgt()-1) \in { PLT, MTL }
        //          || \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()-1))
        //          => getHgt() - getTarget().getHgt() < |getTarget().getCol() - getCol()|)
        //      => getBehaviour() == Down
        if((cell == Cell.LAD || cell == Cell.HDR || (cell_below == Cell.LAD && !guard_below)) &&
           getHgt() > getTarget().getHgt() &&
           (hDist == 0 || hDist > vDist) &&
           getBehaviour() != Move.Down)
            throw new InvariantError("Guard", "getBehaviour() should be Down");
        // inv: getEnvi().getCellNature(getCol(), getHgt()) == LAD
        //      && getHgt() == getTarget().getHgt()
        //      && (getCol() == getTarget().getCol() || |getCol()-getTarget().getCol()| > |getHgt()-getTarget().getHgt()|)
        //      => getBehaviour() == Neutral
        if(cell == Cell.LAD &&
           getHgt() == getTarget().getHgt() &&
           (hDist == 0 || !(cell_below == Cell.PLT || cell_below == Cell.MTL || guard_below)) &&
           getBehaviour() != Move.Neutral)
            throw new InvariantError("Guard", "getBehaviour() should be Neutral");
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
        if((cell_below == Cell.PLT || cell_below == Cell.MTL ||
            (cell_below == Cell.LAD && (cell != Cell.LAD || cell_left == Cell.HDR || cell_belowleft == Cell.PLT || cell_belowleft == Cell.MTL)) || guard_below) &&
           getCol() > getTarget().getCol() &&
           (vDist == 0 || cell != Cell.LAD || hDist < vDist) &&
           getBehaviour() != Move.Left)
            throw new InvariantError("Guard", "getBehaviour() should be Left");
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
        if((cell_below == Cell.PLT || cell_below == Cell.MTL ||
            (cell_below == Cell.LAD && (cell != Cell.LAD || cell_right == Cell.HDR || cell_belowright == Cell.PLT || cell_belowright == Cell.MTL))|| guard_below) &&
           getCol() < getTarget().getCol() &&
           (vDist == 0 || cell != Cell.LAD || hDist < vDist) &&
           getBehaviour() != Move.Right)
            throw new InvariantError("Guard", "getBehaviour() should be Right");
        // inv: (getEnvi().getCellNature(getCol(), getHgt()-1) \in { PLT, MTL }
        //       || (getEnvi().getCellNature(getCol(), getHgt()-1) == LAD
        //           && getEnvi().getCellNature(getCol(), getHgt()) != LAD)
        //       || \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()-1))
        //      && getCol() == getTarget().getCol()
        //      && (getHgt() == getTarget().getHgt() ||
        //                      (getEnvi().getCellNature(getCol(), getHgt()) != LAD
        //                       && (getEnvi().getCellNature(getCol(), getHgt()-1) != Cell.LAD))
        //      => getBehaviour() == Neutral
        if((cell_below == Cell.PLT || cell_below == Cell.MTL ||
            (cell_below == Cell.LAD && cell != Cell.LAD) || guard_below) &&
           getCol() == getTarget().getCol() &&
           (vDist == 0 || (cell != Cell.LAD && (cell_below != Cell.LAD || getHgt() < getTarget().getHgt()))) &&
           getBehaviour() != Move.Neutral)
            throw new InvariantError("Guard", "getBehaviour() should be Neutral"+getBehaviour());
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
        // post: isShot() == false
//        if(isShot())
//            throw new PostconditionError("Guard", "init", "isShot() == false");
    }

    @Override
    public void climbLeft() {
        // pre: getEnvi().getCellNature(getCol(), getHgt()) == HOL
        if(getEnvi().getCellNature(getCol(), getHgt()) != Cell.HOL)
            throw new PreconditionError("Guard", "climbLeft", "getEnvi().getCellNature(getCol(), getHgt()) == HOL");

        // pre-invariant
        checkInvariant();

        // captures
        int col_pre = getCol();
        int hgt_pre = getHgt();
        int id_pre = getId();
        int initCol_pre = getInitCol();
        int initHgt_pre = getInitHgt();
        Character target_pre = getTarget();

        // run
        delegate.climbLeft();

        // post-invariant
        checkInvariant();

        if(getId() != id_pre) throw new PostconditionError("Guard", "climbLeft", "id is supposed to be const");
        if(getInitCol() != initCol_pre) throw new PostconditionError("Guard", "climbLeft", "initCol is supposed to be const");
        if(getInitHgt() != initHgt_pre) throw new PostconditionError("Guard", "climbLeft", "initHgt is supposed to be const");
        if(getTarget() != target_pre) throw new PostconditionError("Guard", "climbLeft", "target is supposed to be const");

        // post: getCol()@pre == 0
        //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
        if(col_pre == 0 &&
           (getCol() != col_pre || getHgt() != hgt_pre))
            throw new PostconditionError("Guard", "climbLeft", "The guard has moved too far left");
        // post: getEnvi().getCellNature(getCol()@pre-1, getHgt()@pre+1) \in { MTL, PLT }
        //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
        if((getEnvi().getCellNature(col_pre-1, hgt_pre+1) == Cell.MTL ||
            getEnvi().getCellNature(col_pre-1, hgt_pre+1) == Cell.PLT) &&
           (getCol() != col_pre || getHgt() != hgt_pre))
            throw new PostconditionError("Guard", "climbLeft", "The guard has climbed into a non-free cell");
        // post: \exists Guard g \in getEnvi().getCellContent(getCol()@pre-1, getHgt()@pre+1)
        //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
        boolean found = false;
        for(InCell ic: getEnvi().getCellContent(col_pre-1, hgt_pre+1)) {
            if(ic instanceof Guard) found = true;
        }
        if(found && (getCol() != col_pre || getHgt() != hgt_pre))
            throw new PostconditionError("Guard", "climbLeft", "The guard has moved into another guard");
        // post: getCol()@pre != 0
        //       && getEnvi().getCellNature(getCol()@pre-1, getHgt()@pre+1) \notin { MTL, PLT }
        //       && \not \exists Guard g \in getEnvi().getCellContent(getCol()@pre-1, getHgt()@pre+1)
        //       => (getCol() == getCol()@pre-1 && getHgt() == getHgt()@pre+1)
        if(col_pre != 0 &&
           getEnvi().getCellNature(col_pre-1, hgt_pre+1) != Cell.MTL &&
           getEnvi().getCellNature(col_pre-1, hgt_pre+1) != Cell.PLT &&
           !found
           && (getCol() != col_pre-1 || getHgt() != hgt_pre+1))
            throw new PostconditionError("Guard", "climbLeft", "The guard has not moved correctly");
    }

    @Override
    public void climbRight() {
        // pre: getEnvi().getCellNature(getCol(), getHgt()) == HOL
        if(getEnvi().getCellNature(getCol(), getHgt()) != Cell.HOL)
            throw new PreconditionError("Guard", "climbRight", "getEnvi().getCellNature(getCol(), getHgt()) == HOL");

        // pre-invariant
        checkInvariant();

        // captures
        int col_pre = getCol();
        int hgt_pre = getHgt();
        int id_pre = getId();
        int initCol_pre = getInitCol();
        int initHgt_pre = getInitHgt();
        Character target_pre = getTarget();

        // run
        delegate.climbRight();

        // post-invariant
        checkInvariant();

        if(getId() != id_pre) throw new PostconditionError("Guard", "climbRight", "id is supposed to be const");
        if(getInitCol() != initCol_pre) throw new PostconditionError("Guard", "climbRight", "initCol is supposed to be const");
        if(getInitHgt() != initHgt_pre) throw new PostconditionError("Guard", "climbRight", "initHgt is supposed to be const");
        if(getTarget() != target_pre) throw new PostconditionError("Guard", "climbRight", "target is supposed to be const");

        // post: getCol()@pre == getEnvi().getWidth()-1
        //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
        if(col_pre == getEnvi().getWidth()-1 &&
           (getCol() != col_pre || getHgt() != hgt_pre))
            throw new PostconditionError("Guard", "climbLeft", "The guard has moved too far left");
        // post: getEnvi().getCellNature(getCol()@pre+1, getHgt()@pre+1) \in { MTL, PLT }
        //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
        if((getEnvi().getCellNature(col_pre+1, hgt_pre+1) == Cell.MTL ||
            getEnvi().getCellNature(col_pre+1, hgt_pre+1) == Cell.PLT) &&
           (getCol() != col_pre || getHgt() != hgt_pre))
            throw new PostconditionError("Guard", "climbRight", "The guard has climbed into a non-free cell");
        // post: \exists Guard g \in getEnvi().getCellContent(getCol()@pre+1, getHgt()@pre+1)
        //       => (getCol() == getCol()@pre && getHgt() == getHgt()@pre)
        boolean found = false;
        for(InCell ic: getEnvi().getCellContent(col_pre+1, hgt_pre+1)) {
            if(ic instanceof Guard) found = true;
        }
        if(found && (getCol() != col_pre || getHgt() != hgt_pre))
            throw new PostconditionError("Guard", "climbRight", "The guard has moved into another guard");
        // post: getCol() != getEnvi().getWidth()-1
        //       && getEnvi().getCellNature(getCol()@pre+1, getHgt()@pre+1) \notin { MTL, PLT }
        //       && \not \exists Guard g \in getEnvi().getCellContent(getCol()@pre+1, getHgt()@pre+1)
        //       => (getCol() == getCol()@pre+1 && getHgt() == getHgt()@pre+1)
        if(col_pre != getEnvi().getWidth()-1 &&
           getEnvi().getCellNature(col_pre+1, hgt_pre+1) != Cell.MTL &&
           getEnvi().getCellNature(col_pre+1, hgt_pre+1) != Cell.PLT &&
           !found
           && (getCol() != col_pre+1 || getHgt() != hgt_pre+1))
            throw new PostconditionError("Guard", "climbRight", "The guard has not moved correctly");
    }

    // def: willFall() = getEnvi().getCellNature(getCol()@pre, getHgt()@pre-1) \in { HOL, EMP, HDR }
    //                   && \not \exists Guard g \in getEnvi().getCellContent(getCol()@pre, getHgt()@pre-1)
    //                   && getEnvi().getCellNature(getCol()@pre, getHgt()@pre) \notin { LAD, HDR }
    private boolean willFall(int col_pre, int hgt_pre) {
        Cell cell_below = getEnvi().getCellNature(col_pre, hgt_pre-1);
        if(cell_below != Cell.HOL && cell_below != Cell.EMP && cell_below != Cell.HDR) return false;
        for(InCell ic: getEnvi().getCellContent(col_pre, hgt_pre-1)) {
            if(ic instanceof Guard) return false;
        }
        Cell c = getEnvi().getCellNature(col_pre, hgt_pre);
        if(c == Cell.LAD || c == Cell.HDR) return false;
        return true;
    }

    @Override
    public void step() {
        // pre-invariant
        checkInvariant();

        // captures
        int col_pre = getCol();
        int hgt_pre = getHgt();
        int timeInHole_pre = 0;
        if(getEnvi().getCellNature(col_pre, hgt_pre) == Cell.HOL) timeInHole_pre = getTimeInHole();
        Move behaviour_pre = getBehaviour();
        Guard delegate_pre = delegate.clone();
        int id_pre = getId();
        int initCol_pre = getInitCol();
        int initHgt_pre = getInitHgt();
        Character target_pre = getTarget();

        // run
        delegate.step();

        // post-invariant
        checkInvariant();

        if(getId() != id_pre) throw new PostconditionError("Guard", "step", "id is supposed to be const");
        if(getInitCol() != initCol_pre) throw new PostconditionError("Guard", "step", "initCol is supposed to be const");
        if(getInitHgt() != initHgt_pre) throw new PostconditionError("Guard", "step", "initHgt is supposed to be const");
        if(getTarget() != target_pre) throw new PostconditionError("Guard", "step", "target is supposed to be const");

        // post: isShot() => getCol() == getInitCol() && getHgt() == getInitHgt() && getTimeInHole() == 0
        if(isShot()) {
            if(!(getCol() == getInitCol()) || !(getHgt() == getInitHgt())) {
                throw new PostconditionError("Guard", "step", "The guard should have been shot");
            }
            return;
        }

        // post: \not isShot() && willFall() => step() == goDown()
        if(willFall(col_pre, hgt_pre)) {
            Guard clone = delegate_pre.clone();
            clone.goDown();
            if(!delegate.equals(clone))
                throw new PostconditionError("Guard", "step", "The guard should have fallen");
        }
        // post: \not isShot() && \not willFall()
        //       && getEnvi().getCellNature(getCol()@pre, getHgt()@pre) == HOL && getTimeInHole()@pre < 5
        //       => getTimeInHole() == getTimeInHole()@pre + 1
        if(!willFall(col_pre, hgt_pre) &&
           getEnvi().getCellNature(col_pre, hgt_pre) == Cell.HOL && timeInHole_pre < 5 &&
           getTimeInHole() != timeInHole_pre + 1)
            throw new PostconditionError("Guard", "step", "The timeInHole has not increased properly");
        // post: \not isShot() && \not willFall()
        //       && getEnvi().getCellNature(getCol()@pre, getHgt()@pre) == HOL && getTimeInHole()@pre == 5
        //       => (getBehaviour()@pre == Left => step() == climbLeft()
        //           && getBehaviour()@pre == Right => step() == climbRight())
        if(!willFall(col_pre, hgt_pre) &&
           getEnvi().getCellNature(col_pre, hgt_pre) == Cell.HOL && timeInHole_pre == 5) {
            Guard clone = delegate_pre.clone();
            if(behaviour_pre == Move.Left) clone.climbLeft();
            if(behaviour_pre == Move.Right) clone.climbRight();
            if(!delegate.equals(clone))
                throw new PostconditionError("Guard", "step", "The guard should have climbed");
        }
        // post: \not isShot() && \not willFall() && getEnvi().getCellNature(getCol()@pre, getHgt()@pre) != HOL
        //       => (getBehaviour()@pre == Left => step() == goLeft()
        //           && getBehaviour()@pre == Right => step() == goRight()
        //           && getBehaviour()@pre == Up => step() == goUp()
        //           && getBehaviour()@pre == Down => step() == goDown())
        if(!willFall(col_pre, hgt_pre) && getEnvi().getCellNature(col_pre, hgt_pre) != Cell.HOL) {
            Guard clone = delegate_pre.clone();
            if(behaviour_pre == Move.Left) clone.goLeft();
            if(behaviour_pre == Move.Right) clone.goRight();
            if(behaviour_pre == Move.Up) clone.goUp();
            if(behaviour_pre == Move.Down) clone.goDown();
            if(!delegate.equals(clone))
                throw new PostconditionError("Guard", "step", "The guard has not followed his behaviour");
        }
    }
}
