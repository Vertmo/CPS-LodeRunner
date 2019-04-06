package loderunner.contracts;

import java.util.HashSet;
import java.util.Set;

import loderunner.contracts.errors.InvariantError;
import loderunner.contracts.errors.PostconditionError;
import loderunner.contracts.errors.PreconditionError;
import loderunner.decorators.EngineDecorator;
import loderunner.services.Cell;
import loderunner.services.Coord;
import loderunner.services.EditableScreen;
import loderunner.services.Engine;
import loderunner.services.Guard;
import loderunner.services.InCell;
import loderunner.services.Item;
import loderunner.services.Player;
import loderunner.services.Status;

public class EngineContract extends EngineDecorator {

    public EngineContract(Engine delegate) {
        super(delegate);
    }

    public void checkInvariant() {
        // inv: getPlayer() \in getEnvironment().getCellContent(getPlayer().getCol(), getPlayer().getHgt())
        //      && \forall x \in [0..getEnvironment().getWidth()[ \forall y \in [0..getEnvironment().getHeight()[
        //           getPlayer() \in getEnvironment().getCellContent(x, y)
        //           => (getPlayer().getCol() == x && getPlayer().getHgt() == y)
        if(!getEnvironment().getCellContent(getPlayer().getCol(), getPlayer().getHgt()).contains(getPlayer()))
            throw new InvariantError("Engine", "The player is not in the correct cell");
        for(int x = 0; x < getEnvironment().getWidth(); x++) {
            for(int y = 0; y < getEnvironment().getHeight(); y++) {
                for(InCell ic: getEnvironment().getCellContent(x, y)) {
                    if(ic instanceof Player &&
                       (x != getPlayer().getCol() || y != getPlayer().getHgt()))
                        throw new InvariantError("Engine", "The player is not in the correct cell");
                }
            }
        }
        // inv: \forall Guard g \in getGuards() g \in getEnvironment().getCellContent(g.getCol(), g.getHgt())
        //      && \forall x \in [0..getEnvironment().getWidth()[ \forall y \in [0..getEnvironment().getHeight()[
        //           \forall Guard g \in getEnvironment().getCellContent(x, y)
        //           => g \in getGuards() && (g.getCol() == x && g.getHgt() == y)
        for(Guard g: getGuards()) {
            if(!getEnvironment().getCellContent(g.getCol(), g.getHgt()).contains(g))
                throw new InvariantError("Engine", "A guard is not in the correct cell");
            for(int x = 0; x < getEnvironment().getWidth(); x++) {
                for(int y = 0; y < getEnvironment().getHeight(); y++) {
                    if(getEnvironment().getCellContent(x, y).contains(g) &&
                        (x != g.getCol() || y != g.getHgt()))
                        throw new InvariantError("Engine", "A guard is not in the correct cell");
                }
            }
        }
        // inv: \forall Item i \in getTreasures() i \in getEnvironment().getCellContent(i.getCol(), i.getHgt())
        //      && \forall x \in [0..getEnvironment().getWidth()[ \forall y \in [0..getEnvironment().getHeight()[
        //           \forall Item i \in getEnvironment().getCellContent(x, y) i.getNature() == Treasure
        //           => i \in getTreasures() && (i.getCol() == x && i.getHgt() == y)
        for(Item i: getTreasures()) {
            if(!getEnvironment().getCellContent(i.getCol(), i.getHgt()).contains(i))
                throw new InvariantError("Engine", "A treasure is not in the correct cell");
            for(int x = 0; x < getEnvironment().getWidth(); x++) {
                for(int y = 0; y < getEnvironment().getHeight(); y++) {
                    if(getEnvironment().getCellContent(x, y).contains(i) &&
                       (x != i.getCol() || y != i.getHgt()))
                        throw new InvariantError("Engine", "A treasure is not in the correct cell");
                }
            }
        }
        // inv: \forall Hole h \in getHoles() h getEnvironment().getCellNature(h.getCol(), g.getHgt()) == HOL
        //      && \forall x \in [0..getEnvironment().getWidth()[ \forall y \in [0..getEnvironment().getHeight()[
        //           getEnvironment().getCellNature(x, y) == HOL
        //           => \exists Hole h \in getHoles() (h.getCol() == x && h.getHgt() == y)
        // TODO
    }

    @Override
    public void init(EditableScreen screen, Coord pCoord, Set<Coord> gCoords, Set<Coord> tCoords) {
        // pre: screen.isPlayable()
        if(!screen.isPlayable())
            throw new PreconditionError("Engine", "init", "screen.isPlayable()");
        // pre: \forall Coord c \in { pCoord } union gCoords union tCoords
        //        screen.getCellNature(c.getCol(), c.getHgt()) == EMP
        if(screen.getCellNature(pCoord.getCol(), pCoord.getHgt()) != Cell.EMP)
            throw new PreconditionError("Engine", "init", "The player is not on an empty cell");
        for(Coord c: gCoords) {
            if(screen.getCellNature(c.getCol(), c.getHgt()) != Cell.EMP)
                throw new PreconditionError("Engine", "init", "A guard is not on an empty cell");
        }
        for(Coord c: tCoords) {
            if(screen.getCellNature(c.getCol(), c.getHgt()) != Cell.EMP)
                throw new PreconditionError("Engine", "init", "A treasure is not on an empty cell");
        }
        // pre: \forall Coord c1 \in gCoords \forall Coord c2 \in gCoords
        //         (c1.getCol() == c2.getCol() && c1.getHgt() == c2.getHgt()) => c1 == c2
        for(Coord c1: gCoords) {
            for(Coord c2: gCoords) {
                if(c1.getCol() == c2.getCol() && c1.getHgt() == c2.getHgt() && c1 != c2)
                    throw new PreconditionError("Engine", "init", "Two guards are on the same cell");
            }
        }
        // pre: \forall Coord c1 \in tCoords \forall Coord c2 \in tCoords
        //         (c1.getCol() == c2.getCol() && c1.getHgt() == c2.getHgt()) => c1 == c2
        for(Coord c1: tCoords) {
            for(Coord c2: tCoords) {
                if(c1.getCol() == c2.getCol() && c1.getHgt() == c2.getHgt() && c1 != c2)
                    throw new PreconditionError("Engine", "init", "Two treasures are on the same cell");
            }
        }
        // pre: \forall Coord c \in gCoords
        //        (c.getCol() != pCoord.getCol() || c.getHgt() != pCoord.getHgt())
        for(Coord c: gCoords) {
            if(c.getCol() == pCoord.getCol() && c.getHgt() == pCoord.getHgt())
                throw new PreconditionError("Engine", "init", "A guard is on the same cell as the player");
        }
        // pre: \forall Coord c \in tCoords
        //        (c.getCol() != pCoord.getCol() || c.getHgt() != pCoord.getHgt())
        for(Coord c: tCoords) {
            if(c.getCol() == pCoord.getCol() && c.getHgt() == pCoord.getHgt())
                throw new PreconditionError("Engine", "init", "A treasure is on the same cell as the player");
        }

        // run
        super.init(screen, pCoord, gCoords, tCoords);

        // post-invariant
        checkInvariant();

        // post: getStatus() == Playing
        if(getStatus() != Status.Playing)
            throw new PostconditionError("Engine", "init", "getStatus() == Playing");
        // post: getLevelScore() == 0
        if(getLevelScore() != 0)
            throw new PostconditionError("Engine", "init", "getLevelScore() == 0");
        // post: getEnvironment().getWidth() == screen.getWidth()
        //       && getEnvironment().getHeight() == screen.getHeight()
        //       &&\forall x \in [0..screen.getWidth()[ \forall y \in [0..screen.getHeight()[
        //         getEnvironment().getCellNature(x, y) == screen.getCellNature(x, y)
        if(getEnvironment().getWidth() != screen.getWidth())
            throw new PostconditionError("Engine", "init", "The width is not the same");
        if(getEnvironment().getHeight() != screen.getHeight())
            throw new PostconditionError("Engine", "init", "The height is not the same");
        for(int x = 0; x < screen.getWidth(); x++) {
            for(int y = 0; y < screen.getHeight(); y++) {
                if(getEnvironment().getCellNature(x, y) != screen.getCellNature(x, y))
                    throw new PostconditionError("Engine", "init", "The content of the environment is not the same");
            }
        }
        // post: getPlayer().getCol() == pCoord.getCol() && getPlayer().getHgt() == pCoord.getHgt()
        if(getPlayer().getCol() != pCoord.getCol() || getPlayer().getHgt() != pCoord.getHgt())
            throw new PostconditionError("Engine", "init", "The coords of the player are not the same");
        // post: \forall Coord c \in gCoords \exists Guard g \in getGuards() (g.getCol() == c.getCol() && g.getHgt() == c.getHgt())
        //       && \forall Guard g \in getGuards() \exists Coord c \in gCoords (g.getCol() == c.getCol() && g.getHgt() == c.getHgt())
        for(Coord c: gCoords) {
            boolean found = false;
            for(Guard g: getGuards()) {
                if(c.getCol() == g.getCol() && c.getHgt() == g.getHgt()) found = true;
            }
            if(!found)
                throw new PostconditionError("Engine", "init", "A guard is missing");
        }
        for(Guard g: getGuards()) {
            boolean found = false;
            for(Coord c: gCoords) {
                if(c.getCol() == g.getCol() && c.getHgt() == g.getHgt()) found = true;
            }
            if(!found)
                throw new PostconditionError("Engine", "init", "There are too much guards");
        }
        // post: \forall Coord c \in tCoords \exists Item i \in getTreasures() (i.getCol() == c.getCol() && i.getHgt() == c.getHgt())
        //       && \forall Item i \in getTreasures() \exists Coord c \in tCoords (i.getCol() == c.getCol() && i.getHgt() == c.getHgt())
        for(Coord c: tCoords) {
            boolean found = false;
            for(Item t: getTreasures()) {
                if(c.getCol() == t.getCol() && c.getHgt() == t.getHgt()) found = true;
            }
            if(!found)
                throw new PostconditionError("Engine", "init", "A treasure is missing");
        }
        for(Item t: getTreasures()) {
            boolean found = false;
            for(Coord c: tCoords) {
                if(c.getCol() == t.getCol() && c.getHgt() == t.getHgt()) found = true;
            }
            if(!found)
                throw new PostconditionError("Engine", "init", "There are too much treasures");
        }
    }

    @Override
    public void step() {
        // pre: getStatus() == Playing
        if(getStatus() != Status.Playing)
            throw new PreconditionError("Engine", "Screen", "getStatus() == Playing");

        // pre-invariant
        checkInvariant();

        // captures
        Set<Item> treasures_pre = new HashSet<>(getTreasures());
        int levelScore_pre = getLevelScore();

        // run
        super.step();

        // post-invariant
        checkInvariant();

        // post: getPlayer() == (getPlayer()@pre).step()
        // TODO
        // post: \forall Guard g: getGuards() g == (g@pre).step()
        // TODO
        // post: \forall Item t \in getTreasures()@pre
        //         \exists Guard g \in getEnvi().getCellContent(t.getCol()@pre, t.getHgt()@pre)@pre
        //           getCellNature(g.getCol(), g.getHgt()) != HOL
        //         => t.getCol() = g.getCol() && t.getHgt() = g.getHgt()
        // TODO
        // post: \forall Item t \in getTreasures()@pre
        //         \exists Guard g \in getEnvi().getCellContent(t.getCol()@pre, t.getHgt()@pre)@pre
        //           getCellNature(g.getCol(), g.getHgt()) == HOL
        //         => t.getCol() = g.getCol() && t.getHgt() = g.getHgt()+1
        // TODO
        // post: \forall Item t \in getTreasures()@pre
        //         \not \exists Guard g \in getEnvi().getCellContent(t.getCol()@pre, t.getHgt()@pre)@pre
        //         => t.getCol() = t.getCol()@pre && t.getHgt() = t.getHgt()@pre
        // TODO
        // post: \exists Item i \in getTreasures()@pre (i.getCol() == getPlayer().getCol() && i.getHgt() == getPlayer().getHgt())
        //       => i \notin getTreasures() && getLevelScore() == getLevelScore()@pre+1
        for(Item i: treasures_pre) {
            if(i.getCol() == getPlayer().getCol() && i.getHgt() == getPlayer().getHgt()
               && (getTreasures().contains(i) || getLevelScore() != levelScore_pre + 1))
                throw new PostconditionError("Engine", "step", "The treasure should have been taken by the player");
        }
        // post: \not exists Item i \in getTreasures()@pre (i.getCol() == getPlayer().getCol() && i.getHgt() == getPlayer().getHgt())
        //       => getLevelScore() == getLevelScore()@pre
        // TODO
        // post: \forall Item i \in getTreasures()@pre (i.getCol() != getPlayer().getCol() || i.getHgt() != getPlayer().getHgt())
        //       => i \in getTreasures()
        for(Item i: treasures_pre)
            if((i.getCol() != getPlayer().getCol() || i.getHgt() != getPlayer().getHgt())
               && !getTreasures().contains(i))
                throw new PostconditionError("Engine", "step", "The treasure should not have been taken by the player");
        // post: getTreasures().isEmpty() => getStatus() == Win
        if(getTreasures().isEmpty() && getStatus() != Status.Win)
            throw new PostconditionError("Engine", "step", "getTreasures().isEmpty() => getStatus() == Win");
        // post: \exists Guard g: getGuards() (g.getCol() == getPlayer().getCol() && g.getHgt() == getPlayer().getHgt())
        //       && !getTreasures().isEmpty()
        //       => getStatus() == Loss
        for(Guard g: getGuards()) {
            if(g.getCol() == getPlayer().getCol() && g.getHgt() == getPlayer().getHgt()
               && !getTreasures().isEmpty()
               && getStatus() != Status.Loss)
                throw new PostconditionError("Engine", "step", "The player should have lost");
        }
        // post: \not getTreasures().isEmpty()
        //       && \not \exists Guard g: getGuards() (g.getCol() == getPlayer().getCol() && g.getHgt() == getPlayer().getHgt())
        //       && \not \exists Hole h \in getHoles()@pre
        //                 (h.getT() == 15 && h.getCol() == getPlayer().getCol()@pre && h.getHgt() == getPlayer().getHgt()@pre)
        //       => getStatus() == Playing
        // TODO
        // post: \forall Hole h \in getHoles()
        //       => h \notin getHoles()@pre => h.getT() == 0
        // TODO
        // post: \forall Hole h \in getHoles()@pre h1.getT()@pre < 15
        //       => h: getHoles() && h.getT() == h.getT()@pre + 1
        // TODO
        // post: \forall Hole h \in getHoles()@pre h.getT() == 15
        //       => (h \notin getHoles()
        //           && (getPlayer.getCol()@pre == h.getCol() && getPlayer()@pre.getHgt() == h.getHgt() => getStatus() == Loss)
        //           && (\exists Guard g \in getEnvironment().getCellContent(h.getCol(), h.getHgt())@pre
        //               => g.getCol() == g.getInitCol() && g.getHgt() == g.getInitHgt()))
        // TODO
    }

    @Override
    public Engine clone() {
        return new EngineContract((Engine)getDelegate().clone());
    }
}
