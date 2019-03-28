package loderunner.contracts;

import loderunner.contracts.errors.InvariantError;
import loderunner.contracts.errors.PostconditionError;
import loderunner.contracts.errors.PreconditionError;
import loderunner.decorators.CharacterDecorator;
import loderunner.services.Cell;
import loderunner.services.Character;
import loderunner.services.Environment;
import loderunner.services.Guard;
import loderunner.services.InCell;

public class CharacterContract extends CharacterDecorator {

    public CharacterContract(Character delegate) {
        super(delegate);
    }

    public void checkInvariant() {
        // inv: getEnvi().getCellNature(getCol(), getHgt()) \in { EMP, HOL, LAD, HDR }
        Cell nat = getEnvi().getCellNature(getCol(), getHgt());
        if(nat != Cell.EMP && nat != Cell.HOL && nat != Cell.LAD && nat != Cell.HDR)
            throw new InvariantError("Character", "The character is in a non-free cell");
    }

    @Override
    public void init(Environment e, int x, int y) {
        // pre: e.getCellNature(x, y) == EMP
        if(e.getCellNature(x, y) != Cell.EMP)
            throw new PreconditionError("Character", "init", "e.getCellNature(x, y) == EMP");

        // run
        super.init(e, x, y);

        // post-invariant
        checkInvariant();

        // post: getEnvi() == e
        if(getEnvi() != e)
            throw new PostconditionError("Character", "init", "getEnvi() == e");

        // post: getCol() == x
        if(getCol() != x)
            throw new PostconditionError("Character", "init", "getCol() == x");

        // post: getHgt() == y
        if(getHgt() != y)
            throw new PostconditionError("Character", "init", "getHgt() == y");
    }

    @Override
    public void goLeft() {
        // pre-invariant
        checkInvariant();

        // captures
        int col_pre = getCol();
        int hgt_pre = getHgt();
        Environment envi_pre = getEnvi();

        // run
        super.goLeft();

        // post-invariant
        checkInvariant();

        // const: envi
        if(getEnvi() != envi_pre)
            throw new PostconditionError("Character", "goLeft", "getEnvi() is supposed to be constant");

        // post: getHgt() == getHgt()@pre
        if(getHgt() != hgt_pre)
            throw new PostconditionError("Character", "goLeft", "getHgt() == getHgt()@pre");

        // post: (getCol()@pre == 0) => getCol() == getCol()@pre
        if(col_pre == 0 && getCol() != col_pre)
            throw new PostconditionError("Character", "goLeft", "The character has moved too far left");

        // si col_pre == 0 on doit arreter de vérifier les autres post conditions parce qu'on y utilise col_pre-1
        // (ArrayOutOfBoundException) + de toute façon on sait déjà qu'elles vont passer si celle ci-dessus est passée
        if(col_pre == 0) return;

        // post: getEnvi().getCellNature(getCol()@pre-1,getHgt()) \in { PLT, MTL }
        //       => getCol() == getCol()@pre
        Cell nat_left = getEnvi().getCellNature(col_pre-1, getHgt());
        if((nat_left == Cell.PLT || nat_left == Cell.MTL) && getCol() != col_pre)
            throw new PostconditionError("Character", "goLeft", "The character has moved into a non-free cell");

        // post: getEnvi().getCellNature(getCol()@pre, getHgt()) \notin { LAD, HDR }
        //       && getEnvi().getCellNature(getCol()@pre, getHgt()-1) \notin { PLT, MTL }
        //       && \not \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()-1)
        //       => getCol() == getCol()@pre
        Cell nat = getEnvi().getCellNature(col_pre, getHgt());
        Cell nat_below = getEnvi().getCellNature(col_pre, getHgt()-1);
        boolean char_below = false;
        for(InCell ic: getEnvi().getCellContent(col_pre, getHgt()-1)) {
            if(ic instanceof Character) char_below = true;
        }
        if(nat != Cell.LAD && nat != Cell.HDR
           && nat_below != Cell.PLT && nat_below != Cell.MTL
           && !char_below && getCol() != col_pre)
            throw new PostconditionError("Character", "goLeft", "The character has moved while not in a position to");

        // post: this \in Guard && \exists Guard g \in getEnvi().getCellContent(getCol()@pre-1, getHgt())
        //       => getCol() == getCol()@pre
        boolean guard_left = false;
        for(InCell ic: getEnvi().getCellContent(col_pre-1, getHgt())) {
            if(ic instanceof Guard) guard_left = true;
        }
        if(this instanceof Guard && guard_left && getCol() != col_pre)
            throw new PostconditionError("Character", "goLeft", "The guard has moved while another was blocking his way");

        // post: getCol()@pre != 0
        //       && getEnvi().getCellNature(getCol()@pre-1 ,getHgt()) \notin { PLT, MTL }
        //       && (getEnvi().getCellNature(getCol()@pre, getHgt()) \in { LAD, HDR }
        //           || getEnvi().getCellNature(getCol()@pre, getHgt()-1) \in { PLT, MTL }
        //           || \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()-1))
        //       && (this \notin Guard || \not \exists Guard g \in getEnvi().getCellContent(getCol()@pre-1, getHgt()))
        //       => getCol() == getCol()@pre - 1
        if(col_pre != 0
           && nat_left != Cell.PLT && nat_left!= Cell.MTL
           && (nat == Cell.LAD || nat == Cell.HDR
               || nat_below == Cell.PLT || nat_below == Cell.MTL
               || char_below)
           && (!(this instanceof Guard) || !guard_left)
           && getCol() != col_pre - 1)
            throw new PostconditionError("Character", "goRight", "The character has not moved when he should have");
    }

    @Override
    public void goRight() {
        // pre-invariant
        checkInvariant();

        // captures
        int col_pre = getCol();
        int hgt_pre = getHgt();
        Environment envi_pre = getEnvi();

        // run
        super.goRight();

        // post-invariant
        checkInvariant();

        // const: envi
        if(getEnvi() != envi_pre)
            throw new PostconditionError("Character", "goRight", "getEnvi() is supposed to be constant");

        // post: getHgt() == getHgt()@pre
        if(getHgt() != hgt_pre)
            throw new PostconditionError("Character", "goRight", "getHgt() == getHgt()@pre");

        // post: (getCol()@pre == getEnvi().getWidth()-1) => getCol() == getCol()@pre
        if(col_pre == getEnvi().getWidth()-1 && getCol() != col_pre)
            throw new PostconditionError("Character", "goRight", "The character has moved too far right");

        // idem que pour goLeft(), on doit et peut arreter de vérifier les postconditions
        if(col_pre == getEnvi().getWidth()-1) return;

        // post: getEnvi().getCellNature(getCol()@pre+1,getHgt()) \in { PLT, MTL }
        //       => getCol() == getCol()@pre
        Cell nat_right = getEnvi().getCellNature(col_pre+1, getHgt());
        if((nat_right == Cell.PLT || nat_right == Cell.MTL) && getCol() != col_pre)
            throw new PostconditionError("Character", "goRight", "The character has moved into a non-free cell");

        // post: getEnvi().getCellNature(getCol()@pre, getHgt()) \notin { LAD, HDR }
        //       && getEnvi().getCellNature(getCol()@pre, getHgt()-1) \notin { PLT, MTL }
        //       && \not \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()-1)
        //       => getCol() == getCol()@pre
        Cell nat = getEnvi().getCellNature(col_pre, getHgt());
        Cell nat_below = getEnvi().getCellNature(col_pre, getHgt()-1);
        boolean char_below = false;
        for(InCell ic: getEnvi().getCellContent(col_pre, getHgt()-1)) {
            if(ic instanceof Character) char_below = true;
        }
        if(nat != Cell.LAD && nat != Cell.HDR
           && nat_below != Cell.PLT && nat_below != Cell.MTL
           && !char_below && getCol() != col_pre)
            throw new PostconditionError("Character", "goRight", "The character has moved while not in a position to");

        // post: this \in Guard && \exists Guard g \in getEnvi().getCellContent(getCol()@pre+1, getHgt())
        //       => getCol() == getCol()@pre
        boolean guard_right = false;
        for(InCell ic: getEnvi().getCellContent(col_pre+1, getHgt())) {
            if(ic instanceof Guard) guard_right = true;
        }
        if(this instanceof Guard && guard_right && getCol() != col_pre)
            throw new PostconditionError("Character", "goRight", "The guard has moved while another was blocking his way");

        // post: getCol()@pre != getEnvi().getWidth()-1
        //       && getEnvi().getCellNature(getCol()@pre+1 ,getHgt()) \notin { PLT, MTL }
        //       && (getEnvi().getCellNature(getCol()@pre, getHgt()) \in { LAD, HDR }
        //           || getEnvi().getCellNature(getCol()@pre, getHgt()-1) \in { PLT, MTL }
        //           || \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()-1))
        //       && (this \notin Guard || \not \exists Guard g \in getEnvi().getCellContent(getCol()@pre+1, getHgt()))
        //       => getCol() == getCol()@pre + 1
        if(col_pre != 0
           && nat_right != Cell.PLT && nat_right != Cell.MTL
           && (nat == Cell.LAD || nat == Cell.HDR
               || nat_below == Cell.PLT || nat_below == Cell.MTL
               || char_below)
           && (!(this instanceof Guard) || !guard_right)
           && getCol() != col_pre + 1)
            throw new PostconditionError("Character", "goRight", "The character has not moved when he should have");
    }

    @Override
    public void goUp() {
        // pre-invariant
        checkInvariant();

        // captures
        int col_pre = getCol();
        int hgt_pre = getHgt();
        Environment envi_pre = getEnvi();

        // run
        super.goUp();

        // post-invariant
        checkInvariant();

        // const: envi
        if(getEnvi() != envi_pre)
            throw new PostconditionError("Character", "goUp", "getEnvi() is supposed to be constant");

        // post: getCol() == getCol()@pre
        if(getCol() != col_pre)
            throw new PostconditionError("Character", "goUp", "getCol() == getCol()@pre");

        // post: (getHgt()@pre == getEnvi().getHeight()-1) => getHgt() == getHgt()@pre
        if(hgt_pre == getEnvi().getHeight()-1 && getHgt() != hgt_pre)
            throw new PostconditionError("Character", "goUp", "The character has moved too far up");

        // idem que pour goLeft()
        if(hgt_pre == getEnvi().getHeight()-1) return;

        Cell nat_above = getEnvi().getCellNature(getCol(), hgt_pre+1);
        boolean guard_above = false;
        for(InCell ic: getEnvi().getCellContent(getCol(), hgt_pre+1)) {
            if(ic instanceof Guard) guard_above = true;
        }
        // post: getEnvi().getCellNature(getCol(), getHgt()@pre) == LAD
        //       && getEnvi().getCellNature(getCol(), getHgt()@pre+1) \in { EMP, HOL, LAD, HDR }
        //       && (this \notin Guard || \not \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()@pre+1))
        //       => getHgt() == getHgt()@pre + 1
        if(getEnvi().getCellNature(getCol(), hgt_pre) == Cell.LAD
           && (nat_above == Cell.EMP || nat_above == Cell.HOL || nat_above == Cell.LAD || nat_above == Cell.HDR)
           && (!(this instanceof Guard) || !guard_above)
           && getHgt() != hgt_pre+1)
            throw new PostconditionError("Character", "goUp", "The character has not moved when he should have");

        // post: getEnvi().getCellNature(getCol(), getHgt()@pre) != LAD
        //       || getEnvi().getCellNature(getCol(), getHgt()@pre+1) \notin { EMP, HOL, LAD, HDR }
        //       || (this \in Guard && \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()@pre+1))
        //       => getHgt() == getHgt()@pre
        if((getEnvi().getCellNature(getCol(), hgt_pre) != Cell.LAD
            || (nat_above != Cell.EMP && nat_above != Cell.HOL && nat_above != Cell.LAD && nat_above != Cell.HDR)
            || (this instanceof Guard && guard_above))
           && getHgt() != hgt_pre)
            throw new PostconditionError("Character", "goUp", "The character has moved when he should not have");
    }

    @Override
    public void goDown() {
        // pre-invariant
        checkInvariant();

        // captures
        int col_pre = getCol();
        int hgt_pre = getHgt();
        Environment envi_pre = getEnvi();

        // run
        super.goDown();

        // post-invariant
        checkInvariant();

        // const: envi
        if(getEnvi() != envi_pre)
            throw new PostconditionError("Character", "goDown", "getEnvi() is supposed to be constant");

        // post: (getCol() == getCol()@pre
        if(getCol() != col_pre)
            throw new PostconditionError("Character", "goDown", "getCol() == getCol()@pre");

        // post: (getHgt()@pre == 0) => getHgt() == getHgt()@pre
        if(hgt_pre == 0 && getHgt() != hgt_pre)
            throw new PostconditionError("Character", "goDown", "The character has moved too far down");

        // idem que pour goLeft
        if(hgt_pre == 0) return;

        Cell nat_below = getEnvi().getCellNature(getCol(), hgt_pre-1);
        boolean guard_below = false;
        for(InCell ic: getEnvi().getCellContent(getCol(), hgt_pre-1)) {
            if(ic instanceof Guard) guard_below = true;
        }
        // post: getEnvi().getCellNature(getCol(), getHgt()@pre-1) \in { EMP, HOL, LAD, HDR }
        //       && \not \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()@pre-1)
        //       => getHgt() == getHgt()@pre - 1
        if((nat_below == Cell.EMP || nat_below == Cell.HOL || nat_below == Cell.LAD || nat_below == Cell.HDR)
           && !guard_below
           && getHgt() != hgt_pre-1)
            throw new PostconditionError("Character", "goDown", "The character has not moved when he should have");

        // post: getEnvi().getCellNature(getCol(), getHgt()@pre-1) \notin { EMP, HOL, LAD, HDR }
        //       || \exists Guard g \in getEnvi().getCellContent(getCol(), getHgt()@pre-1))
        //       => getHgt() == getHgt()@pre
        if(((nat_below != Cell.EMP && nat_below != Cell.HOL && nat_below != Cell.LAD && nat_below != Cell.HDR)
            || guard_below)
           && getHgt() != hgt_pre)
            throw new PostconditionError("Character", "goDown", "The character has moved when he should not have");
    }
}
