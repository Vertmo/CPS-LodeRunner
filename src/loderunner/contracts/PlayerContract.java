package loderunner.contracts;

import loderunner.contracts.errors.PostconditionError;
import loderunner.contracts.errors.PreconditionError;
import loderunner.services.Cell;
import loderunner.services.Character;
import loderunner.services.Command;
import loderunner.services.Engine;
import loderunner.services.Environment;
import loderunner.services.Guard;
import loderunner.services.GunShot;
import loderunner.services.InCell;
import loderunner.services.Player;

public class PlayerContract extends CharacterContract implements Player{
    private final Player delegate;


    public PlayerContract(Player delegate) {
        super(delegate);
        this.delegate = delegate;
    }


    @Override
    public Player clone() {
        return new PlayerContract(delegate.clone());
    }


    @Override
    public Engine getEngine() {
        return delegate.getEngine();
    }

    @Override
    public int getNbKeys() {
        return delegate.getNbKeys();
    }

    @Override
    public void init(Environment e, Engine eg, int x, int y) {
        // pre: e.getCellNature(x, y) == EMP
        if(!(e.getCellNature(x, y) == Cell.EMP))
            throw new PreconditionError("Player", "init", "e.getCellNature(x, y) == EMP");

        // run
        delegate.init(e, eg, x, y);

        // post-invariant
        checkInvariant();

        // post: getEnvi() == e
        if(!(getEnvi() == e))
            throw new PostconditionError("Player", "init", "getEnvi() == e");

        // post: getCol() == x
        if(!(getCol() == x))
            throw new PostconditionError("Player", "init", "getCol() == x");

        // post: getHgt() == y
        if(!(getHgt() == y))
            throw new PostconditionError("Player", "init", "getHgt() == y");

        // post: getEngine() == eg
        if(!(getEngine() == eg))
            throw new PostconditionError("Player", "init", "getEngine() == eg");

        // post: getNbKeys() == 0
        if(getNbKeys() != 0)
            throw new PostconditionError("Player", "init", "getNbKeys() == 0");
    }


    // def: willFall() = getEnvi().getCellNature(getCol()@pre, getHgt()@pre-1) \in { HOL, EMP, HDR }
    //                   && \not \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()@pre-1)
    //                   && getEnvi().getCellNature(getCol()@pre, getHgt()@pre) \notin { LAD, HDR }
    private boolean willFall(int col_pre, int hgt_pre) {
        Cell cell_below = getEnvi().getCellNature(col_pre, hgt_pre-1);
        if(cell_below == Cell.MTL || cell_below == Cell.PLT || cell_below == Cell.LAD ) return false;
        for(InCell ic: getEnvi().getCellContent(col_pre, hgt_pre-1)) {
            if(ic instanceof Character) return false;
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
        int nbKeys_pre = getNbKeys();
        Engine engine_pre = getEngine();
        Cell down_nat_pre = getEnvi().getCellNature(col_pre, hgt_pre-1);
        Cell left_nat_pre = null; //capturer seulement si la cellule existe
        Cell right_nat_pre = null; //capturer seulement si la cellule existe
        Cell down_left_nat_pre = null; //capturer seulement si la cellule existe
        Cell down_right_nat_pre = null; //capturer seulement si la cellule existe
        boolean left_content_is_empty_pre = false; //capturer seulement si la cellule existe
        boolean right_content_is_empty_pre = false; //capturer seulement si la cellule existe
        boolean down_character_present_pre = false;
        if(col_pre != 0) {
            left_nat_pre = getEnvi().getCellNature(col_pre-1, hgt_pre);
            down_left_nat_pre = getEnvi().getCellNature(col_pre-1, hgt_pre-1);
            left_content_is_empty_pre = getEnvi().getCellContent(col_pre-1, hgt_pre).isEmpty();
        }
        if(col_pre != getEnvi().getWidth()-1) {
            right_nat_pre = getEnvi().getCellNature(col_pre+1, hgt_pre);
            down_right_nat_pre = getEnvi().getCellNature(col_pre+1, hgt_pre-1);
            right_content_is_empty_pre = getEnvi().getCellContent(col_pre+1, hgt_pre).isEmpty();
        }
        for(InCell c : getEnvi().getCellContent(col_pre, hgt_pre-1)) {
            if(c instanceof Character) {
                down_character_present_pre = true;
                break;
            }
        }
        Command cmd_pre = getEngine().peekNextCommand();
        Player delegate_pre = delegate.clone();


        // run
        delegate.step();


        // post-invariant
        checkInvariant();


        if(!(getEngine().equals(engine_pre))) throw new PostconditionError("Player", "step", "engine is supposed to be const");


        // post: willFall() => step() == goDown()
        if(willFall(col_pre, hgt_pre)) {
            Player clone = delegate_pre.clone();
            clone.goDown();
            if(!delegate.equals(clone))
                throw new PostconditionError("Player", "step", "The Player should have fallen");
            return;
        }

        // post: \not willFall() =>
        //       (getEngine().getNextCommand() == Left => step() == goLeft()
        //        && getEngine().getNextCommand() == Right => step() == goRight()
        //        && getEngine().getNextCommand() == Up => step() == goUp()
        //        && getEngine().getNextCommand() == Down => step() == goDown())
        Player clone = delegate_pre.clone();

        if(cmd_pre == Command.Left) {
            clone.goLeft();
            if(!delegate.equals(clone))
                throw new PostconditionError("Player", "step", "The player should have go left");
            return;
        }
        if(cmd_pre == Command.Right) {
            clone.goRight();
            if(!delegate.equals(clone))
                throw new PostconditionError("Player", "step", "The player should have gone right");
            return;
        }
        if(cmd_pre == Command.Up) {
            clone.goUp();
            if(!delegate.equals(clone))
                throw new PostconditionError("Player", "step", "The player should have gone up");
            return;
        }
        if(cmd_pre == Command.Down) {
            clone.goDown();
            if(!delegate.equals(clone))
                throw new PostconditionError("Player", "step", "The player should have gone down");
            return;
        }

        // post: getEngine().getNextCommand() \in { DigL, DigR, Neutral }
        //       => getCol() == getCol()@pre && getHgt() == getHgt()@pre
        if(cmd_pre == Command.DigL || cmd_pre == Command.DigR || cmd_pre == Command.Neutral) {
            if(!(getCol() == col_pre && getHgt() == hgt_pre))
                throw new PostconditionError("Player", "step", "The player shouldn't have moved");
        }

        // post: \not willFall()
        //       && getEngine().getNextCommand() == DigL
        //       && getEngine().getNumberBullets() == 0
        //       && (getEnvi().getCellNature(getCol()@pre, getHgt()@pre-1) \in { PLT, MTL, LAD}
        //           || \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()@pre-1))
        //       && getEnvi().getCellNature(getCol()@pre-1, getHgt()@pre) \in { EMP, HOL }
        //       && getEnvi().getCellContent(getCol()@pre-1,getHgt()@pre).isEmpty()
        //       && getEnvi().getCellNature(getCol()@pre-1, getHgt()@pre-1)@pre == PLT
        //       => getEnvi().getCellNature(getCol()@pre-1, getHgt()@pre-1) == HOL
        if(cmd_pre == Command.DigL && getEngine().getNumberBullets() == 0 && col_pre != 0
           && (down_nat_pre == Cell.PLT || down_nat_pre == Cell.MTL || down_nat_pre == Cell.LAD ||
               down_character_present_pre)
           && (left_nat_pre == Cell.EMP || left_nat_pre == Cell.HOL)
           && left_content_is_empty_pre
           && down_left_nat_pre == Cell.PLT) {
            if(!(getEnvi().getCellNature(col_pre-1, hgt_pre-1) == Cell.HOL))
                throw new PostconditionError("Player", "step", "The player should have dig left");
            return;
        }

        // post: \not willFall()
        //       && getEngine().getNextCommand() == DigL
        //       && (getEnvi().getCellNature(getCol()@pre, getHgt()@pre-1) \in { PLT, MTL, LAD }
        //           || \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()@pre-1))
        //       && getEnvi().getCellNature(getCol()@pre-1, getHgt()@pre) = DOR
        //       && getNbKeys()@pre > 0
        //       => (getEnvi().getCellNature(getCol()@pre-1, getHgt()@pre) == EMP && getNbKeys() == getNbKeys()@pre-1)
        if(cmd_pre == Command.DigL && col_pre != 0
           && (down_nat_pre == Cell.PLT || down_nat_pre == Cell.MTL || down_nat_pre == Cell.LAD ||
               down_character_present_pre)
           && left_nat_pre == Cell.DOR && nbKeys_pre > 0
           && (getEnvi().getCellNature(col_pre-1, hgt_pre) != Cell.EMP || getNbKeys() != nbKeys_pre-1))
            throw new PostconditionError("Player", "step", "The left door has not been opened correctly");
                
        // post: \not willFall()
        //       && getEngine().getNextCommand() == DigR
        //       && getEngine().getNumberBullets() == 0
        //       && (getEnvi().getCellNature(getCol()@pre, getHgt()@pre-1) \in { PLT, MTL, LAD }
        //           || \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()@pre-1))
        //       && getEnvi().getCellNature(getCol()@pre+1, getHgt()@pre) \in { EMP, HOL }
        //       && getEnvi().getCellContent(getCol()@pre+1,getHgt()@pre).isEmpty()
        //       && getEnvi().getCellNature(getCol()@pre+1, getHgt()@pre-1)@pre == PLT
        //       => getEnvi().getCellNature(getCol()@pre+1, getHgt()@pre-1) == HOL
        if(cmd_pre == Command.DigR && getEngine().getNumberBullets() == 0 && col_pre != getEnvi().getWidth()-1
           && (down_nat_pre == Cell.PLT || down_nat_pre == Cell.MTL || down_nat_pre == Cell.LAD ||
               down_character_present_pre)
           && (right_nat_pre == Cell.EMP || right_nat_pre == Cell.HOL)
           && right_content_is_empty_pre
           && down_right_nat_pre == Cell.PLT) {
            if(!(getEnvi().getCellNature(col_pre+1, hgt_pre-1) == Cell.HOL))
                throw new PostconditionError("Player", "step", "The player should have dig right");
            return;
        }

        // post: \not willFall()
        //       && getEngine().getNextCommand() == DigL
        //       && (getEnvi().getCellNature(getCol()@pre, getHgt()@pre-1) \in { PLT, MTL, LAD }
        //           || \exists Character c \in getEnvi().getCellContent(getCol()@pre, getHgt()@pre-1))
        //       && getEnvi().getCellNature(getCol()@pre-1, getHgt()@pre) = DOR
        //       && getNbKeys()@pre > 0
        //       => (getEnvi().getCellNature(getCol()@pre-1, getHgt()@pre) == EMP && getNbKeys() == getNbKeys()@pre-1)
        if(cmd_pre == Command.DigL && col_pre != 0
           && (down_nat_pre == Cell.PLT || down_nat_pre == Cell.MTL || down_nat_pre == Cell.LAD ||
               down_character_present_pre)
           && right_nat_pre == Cell.DOR && nbKeys_pre > 0
           && (getEnvi().getCellNature(col_pre+1, hgt_pre) != Cell.EMP || getNbKeys() != nbKeys_pre-1))
            throw new PostconditionError("Player", "step", "The right door has not been opened correctly");

        // post: \not willFall()
        //       && getEngine().getNextCommand() == ShootL
        //       && getEngine().getNumberBullets() > 0
        //       && (\exist j \in [0, getCol()@pre[,
        //                     \exist Guard g1 \in getEnvi().getCellContent(j,getHgt()@pre)
        //                     && \forAll k \in ]j,getCol()@pre[, getEnvi().getCellNature(k,getHgt()@pre) \in {EMP,HOL,LAD,HDR}
        //                                                        && \not \exist Guard g2 \in getEnvi.getCellContent(k,getHgt()@pre))
        //       => \exist Gunshot gs \in getEnvi().getCellContent(j,getHgt()@pre)
        if(cmd_pre == Command.ShootL && getEngine().getNumberBullets() > 0) {
            for(int j=col_pre-1;j>=0;j--) {
                Cell cell_nat = getEnvi().getCellNature(j, hgt_pre);
                if(cell_nat == Cell.MTL || cell_nat == Cell.PLT)
                    return;
                for(InCell content : getEnvi().getCellContent(j, hgt_pre)){
                    if(content instanceof Guard) {
                        for(InCell shot : getEnvi().getCellContent(j, hgt_pre)) {
                            if(shot instanceof GunShot) {
                                return;
                            }
                        }
                        throw new PostconditionError("Player", "step", "The player should have shot to the left");
                    }
                }
            }
            return;

            // post: \not willFall()
            //       && getEngine().getNextCommand() == ShootR
            //       && getEngine().getNumberBullets() > 0
            //       && (\exist j \in ]getCol()@pre, getEnvi().getWidth()[,
            //                     \exist Guard g1 \in getEnvi().getCellContent(j,getHgt()@pre)
            //                     && \forAll k \in ]getCol()@pre,j[, getEnvi().getCellNature(k,getHgt()@pre) \in {EMP,HOL,LAD,HDR}
            //                                                        && \not \exist Guard g2 \in getEnvi.getCellContent(k,getHgt()@pre))
            //       => \exist Gunshot gs \in getEnvi().getCellContent(j,getHgt()@pre)
            if(cmd_pre == Command.ShootR && getEngine().getNumberBullets() > 0) {
                for(int j=col_pre+1;j<getEnvi().getWidth();j++) {
                    Cell cell_nat = getEnvi().getCellNature(j, hgt_pre);
                    if(cell_nat == Cell.MTL || cell_nat == Cell.PLT)
                        return;
                    for(InCell content : getEnvi().getCellContent(j, hgt_pre)){
                        if(content instanceof Guard) {
                            for(InCell shot : getEnvi().getCellContent(j, hgt_pre)) {
                                if(shot instanceof GunShot) {
                                    return;
                                }
                            }
                            throw new PostconditionError("Player", "step", "The player should have shot to the right");
                        }
                    }
                }
                return;
            }
        }
    }

    @Override
    public void teleport(int x, int y) {
        // pre: getEnvi().getCellNature(x, y) == EMP
        if(getEnvi().getCellNature(x, y) != Cell.EMP)
            throw new PreconditionError("Player", "teleport", "getEnvi().getCellNature(x, y) == EMP");

        // pre-invariant
        checkInvariant();

        // captures
        int nbKeys_pre = getNbKeys();

        // run
        delegate.teleport(x, y);

        // post-invariant
        checkInvariant();

        // post: getCol() == x
        if(getCol() != x) throw new PostconditionError("Player", "teleport", "getCol() == x");
        // post: getHgt() == y
        if(getHgt() != y) throw new PostconditionError("Player", "teleport", "getHgt() == y");

        // post: getNbKeys() == getNbKeys()@pre
        if(getNbKeys() != nbKeys_pre)
            throw new PostconditionError("Player", "teleport", "etNbKeys() == getNbKeys()@pre");
    }

    @Override
    public void grabKey() {
        // pre-invariant
        checkInvariant();
            
        // captures
        int nbKeys_pre = getNbKeys();
            
        // run
        delegate.grabKey();

        // post-invariant
        checkInvariant();

        // post: getNbKeys() = getNbKeys()@pre + 1
        if(getNbKeys() != nbKeys_pre + 1)
            throw new PostconditionError("Player", "grabKey", "getNbKeys() = getNbKeys()@pre + 1");
    }
}
